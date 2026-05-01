package com.microfinance.core_banking.service.credit;

import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.client.UtilisateurRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.credit.*;
import com.microfinance.core_banking.service.operation.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CreditServiceImpl implements CreditService {

	private final DemandeCreditRepository demandeCreditRepository;
	private final CreditRepository creditRepository;
	private final EcheanceRepository echeanceRepository;
	private final ProduitCreditRepository produitCreditRepository;
	private final StatutCreditRepository statutCreditRepository;
	private final ClientRepository clientRepository;
	private final UtilisateurRepository utilisateurRepository;
	private final CompteRepository compteRepository;
	private final AmortissementService amortissementService;
	private final TransactionService transactionService;

	public CreditServiceImpl(
			DemandeCreditRepository demandeCreditRepository,
			CreditRepository creditRepository,
			EcheanceRepository echeanceRepository,
			ProduitCreditRepository produitCreditRepository,
			StatutCreditRepository statutCreditRepository,
			ClientRepository clientRepository,
			UtilisateurRepository utilisateurRepository,
			CompteRepository compteRepository,
			AmortissementService amortissementService,
			TransactionService transactionService
	) {
		this.demandeCreditRepository = demandeCreditRepository;
		this.creditRepository = creditRepository;
		this.echeanceRepository = echeanceRepository;
		this.produitCreditRepository = produitCreditRepository;
		this.statutCreditRepository = statutCreditRepository;
		this.clientRepository = clientRepository;
		this.utilisateurRepository = utilisateurRepository;
		this.compteRepository = compteRepository;
		this.amortissementService = amortissementService;
		this.transactionService = transactionService;
	}

	@Override
	@Transactional
	public DemandeCredit soumettreDemandeCredit(Long idClient, String codeProduitCredit,
												BigDecimal montantDemande, Integer dureeSouhaitee,
												String objetCredit, Long idAgentCredit) {

		Client client = clientRepository.findById(idClient)
				.orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + idClient));

		ProduitCredit produit = produitCreditRepository.findByCodeProduit(codeProduitCredit)
				.orElseThrow(() -> new EntityNotFoundException("Produit de credit introuvable: " + codeProduitCredit));

		if (!produit.getEstActif()) {
			throw new IllegalStateException("Le produit de credit '" + codeProduitCredit + "' n'est plus commercialise.");
		}

		// Validation du montant dans les bornes du produit
		if (montantDemande.compareTo(produit.getMontantMin()) < 0) {
			throw new IllegalArgumentException("Le montant minimum pour ce produit est de " + produit.getMontantMin() + " FCFA.");
		}
		if (montantDemande.compareTo(produit.getMontantMax()) > 0) {
			throw new IllegalArgumentException("Le montant maximum pour ce produit est de " + produit.getMontantMax() + " FCFA.");
		}

		// Validation de la duree dans les bornes du produit
		if (dureeSouhaitee < produit.getDureeMinMois()) {
			throw new IllegalArgumentException("La duree minimale pour ce produit est de " + produit.getDureeMinMois() + " mois.");
		}
		if (dureeSouhaitee > produit.getDureeMaxMois()) {
			throw new IllegalArgumentException("La duree maximale pour ce produit est de " + produit.getDureeMaxMois() + " mois.");
		}

		DemandeCredit demande = new DemandeCredit();
		demande.setReferenceDemande(genererReferenceDemande());
		demande.setClient(client);
		demande.setProduitCredit(produit);
		demande.setMontantDemande(montantDemande);
		demande.setDureeSouhaitee(dureeSouhaitee);
		demande.setObjetCredit(objetCredit);
		demande.setDateDemande(LocalDate.now());
		demande.setStatutDemande(StatutDemande.EN_ATTENTE);

		// Assignation de l'agent de credit si fourni
		if (idAgentCredit != null) {
			Utilisateur agent = utilisateurRepository.findById(idAgentCredit)
					.orElseThrow(() -> new EntityNotFoundException("Agent de credit introuvable: " + idAgentCredit));
			demande.setAgentCredit(agent);
		}

		return demandeCreditRepository.save(demande);
	}

	@Override
	@Transactional
	public Credit approuverDemande(Long idDemande) {
		DemandeCredit demande = demandeCreditRepository.findById(idDemande)
				.orElseThrow(() -> new EntityNotFoundException("Demande introuvable: " + idDemande));

		if (demande.getStatutDemande() != StatutDemande.EN_ATTENTE
				&& demande.getStatutDemande() != StatutDemande.EN_ETUDE) {
			throw new IllegalStateException("Seule une demande EN_ATTENTE ou EN_ETUDE peut etre approuvee. Statut actuel: " + demande.getStatutDemande());
		}

		demande.setStatutDemande(StatutDemande.APPROUVEE);
		demande.setDateDecision(LocalDateTime.now());
		demandeCreditRepository.save(demande);

		// Creation du credit associe
		ProduitCredit produit = demande.getProduitCredit();

		StatutCredit statutApprouve = statutCreditRepository.findByCodeStatut("APPROUVE")
				.orElseThrow(() -> new IllegalStateException("Alerte Systeme : Le statut 'APPROUVE' n'est pas configure en base."));

		Credit credit = new Credit();
		credit.setReferenceCredit(genererReferenceCredit());
		credit.setClient(demande.getClient());
		credit.setProduitCredit(produit);
		credit.setMontantAccorde(demande.getMontantDemande());
		credit.setMontantRestantDu(demande.getMontantDemande());
		credit.setTauxInteretAnnuel(produit.getTauxInteretAnnuel());
		credit.setDureeMois(demande.getDureeSouhaitee());
		credit.setMethodeCalcul(produit.getMethodeCalcul());
		credit.setStatutCredit(statutApprouve);
		credit.setDemandeCredit(demande);

		// Calcul des frais de dossier
		if (produit.getFraisDossierPourcentage() != null) {
			BigDecimal frais = demande.getMontantDemande()
					.multiply(produit.getFraisDossierPourcentage())
					.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
			credit.setFraisDossier(frais);
		} else {
			credit.setFraisDossier(BigDecimal.ZERO);
		}

		return creditRepository.save(credit);
	}

	@Override
	@Transactional
	public DemandeCredit rejeterDemande(Long idDemande, String motifRejet) {
		DemandeCredit demande = demandeCreditRepository.findById(idDemande)
				.orElseThrow(() -> new EntityNotFoundException("Demande introuvable: " + idDemande));

		if (demande.getStatutDemande() != StatutDemande.EN_ATTENTE
				&& demande.getStatutDemande() != StatutDemande.EN_ETUDE) {
			throw new IllegalStateException("Seule une demande EN_ATTENTE ou EN_ETUDE peut etre rejetee.");
		}

		if (motifRejet == null || motifRejet.isBlank()) {
			throw new IllegalArgumentException("Le motif de rejet est obligatoire.");
		}

		demande.setStatutDemande(StatutDemande.REJETEE);
		demande.setMotifRejet(motifRejet);
		demande.setDateDecision(LocalDateTime.now());
		return demandeCreditRepository.save(demande);
	}

	@Override
	@Transactional
	public Credit decaisserCredit(Long idCredit, String numCompteCible) {
		Credit credit = creditRepository.findById(idCredit)
				.orElseThrow(() -> new EntityNotFoundException("Credit introuvable: " + idCredit));

		StatutCredit statutApprouve = statutCreditRepository.findByCodeStatut("APPROUVE")
				.orElseThrow(() -> new IllegalStateException("Statut APPROUVE non configure."));

		if (!credit.getStatutCredit().getCodeStatut().equals("APPROUVE")) {
			throw new IllegalStateException("Seul un credit APPROUVE peut etre decaisse. Statut actuel: "
					+ credit.getStatutCredit().getCodeStatut());
		}

		Compte compte = compteRepository.findByNumCompte(numCompteCible)
				.orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + numCompteCible));

		// Verifier que le compte appartient au client du credit
		if (!compte.getClient().getIdClient().equals(credit.getClient().getIdClient())) {
			throw new IllegalArgumentException("Le compte " + numCompteCible
					+ " n'appartient pas au client du credit.");
		}

		// Montant net = montant accorde - frais de dossier
		BigDecimal montantNet = credit.getMontantAccorde().subtract(
				credit.getFraisDossier() != null ? credit.getFraisDossier() : BigDecimal.ZERO
		);

		// Decaissement via le moteur transactionnel existant
		// On utilise un utilisateur systeme (id=1) pour le decaissement automatique
		transactionService.faireDepot(numCompteCible, montantNet, 1L);

		// Mise a jour du credit
		credit.setDateDecaissement(LocalDate.now());
		credit.setDateFinPrevue(LocalDate.now().plusMonths(credit.getDureeMois()));
		credit.setCompteDecaissement(compte);

		StatutCredit statutDecaisse = statutCreditRepository.findByCodeStatut("DECAISSE")
				.orElseThrow(() -> new IllegalStateException("Statut DECAISSE non configure."));
		credit.setStatutCredit(statutDecaisse);

		// Generation du tableau d'amortissement
		List<Echeance> echeances = amortissementService.genererTableau(
				credit.getMontantAccorde(),
				credit.getTauxInteretAnnuel(),
				credit.getDureeMois(),
				credit.getMethodeCalcul(),
				credit.getDateDecaissement()
		);

		// Rattachement des echeances au credit
		for (Echeance echeance : echeances) {
			echeance.setCredit(credit);
		}
		credit.setEcheances(echeances);

		return creditRepository.save(credit);
	}

	@Override
	@Transactional
	public Credit enregistrerRemboursement(Long idCredit, BigDecimal montant) {
		if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Le montant du remboursement doit etre strictement positif.");
		}

		Credit credit = creditRepository.findById(idCredit)
				.orElseThrow(() -> new EntityNotFoundException("Credit introuvable: " + idCredit));

		String statutCode = credit.getStatutCredit().getCodeStatut();
		if (!statutCode.equals("DECAISSE") && !statutCode.equals("EN_COURS") && !statutCode.equals("EN_RETARD")) {
			throw new IllegalStateException("Le credit n'est pas dans un etat permettant le remboursement.");
		}

		// Imputation sur les echeances impayees en ordre chronologique
		List<Echeance> echeancesImpayees = echeanceRepository
				.findByCredit_IdCreditAndEstPayeeFalseOrderByNumeroEcheanceAsc(idCredit);

		if (echeancesImpayees.isEmpty()) {
			throw new IllegalStateException("Toutes les echeances sont deja payees.");
		}

		BigDecimal montantRestant = montant;

		for (Echeance echeance : echeancesImpayees) {
			if (montantRestant.compareTo(BigDecimal.ZERO) <= 0) break;

			BigDecimal resteDu = echeance.getMontantTotal().subtract(echeance.getMontantPaye());

			if (montantRestant.compareTo(resteDu) >= 0) {
				// Paiement complet de cette echeance
				echeance.setMontantPaye(echeance.getMontantTotal());
				echeance.setEstPayee(true);
				echeance.setDatePaiement(LocalDate.now());
				montantRestant = montantRestant.subtract(resteDu);
			} else {
				// Paiement partiel
				echeance.setMontantPaye(echeance.getMontantPaye().add(montantRestant));
				montantRestant = BigDecimal.ZERO;
			}
			echeanceRepository.save(echeance);
		}

		// Mise a jour du montant restant du
		credit.setMontantRestantDu(credit.getMontantRestantDu().subtract(montant.subtract(montantRestant)));

		// Verification si toutes les echeances sont soldees
		long echeancesRestantes = echeanceRepository.countByCredit_IdCreditAndEstPayeeFalse(idCredit);
		if (echeancesRestantes == 0) {
			StatutCredit statutSolde = statutCreditRepository.findByCodeStatut("SOLDE")
					.orElseThrow(() -> new IllegalStateException("Statut SOLDE non configure."));
			credit.setStatutCredit(statutSolde);
			credit.setMontantRestantDu(BigDecimal.ZERO);
		} else {
			// Passer en EN_COURS si c'etait le premier remboursement
			if (credit.getStatutCredit().getCodeStatut().equals("DECAISSE")) {
				StatutCredit statutEnCours = statutCreditRepository.findByCodeStatut("EN_COURS")
						.orElseThrow(() -> new IllegalStateException("Statut EN_COURS non configure."));
				credit.setStatutCredit(statutEnCours);
			}
		}

		return creditRepository.save(credit);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Echeance> consulterTableauAmortissement(Long idCredit) {
		if (!creditRepository.existsById(idCredit)) {
			throw new EntityNotFoundException("Credit introuvable: " + idCredit);
		}
		return echeanceRepository.findByCredit_IdCreditOrderByNumeroEcheanceAsc(idCredit);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Credit> consulterCreditsClient(Long idClient, Pageable pageable) {
		if (!clientRepository.existsById(idClient)) {
			throw new EntityNotFoundException("Client introuvable: " + idClient);
		}
		return creditRepository.findByClient_IdClient(idClient, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Credit consulterCredit(Long idCredit) {
		return creditRepository.findById(idCredit)
				.orElseThrow(() -> new EntityNotFoundException("Credit introuvable: " + idCredit));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<DemandeCredit> listerDemandesEnAttente(Pageable pageable) {
		return demandeCreditRepository.findByStatutDemande(StatutDemande.EN_ATTENTE, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public DemandeCredit consulterDemande(Long idDemande) {
		return demandeCreditRepository.findById(idDemande)
				.orElseThrow(() -> new EntityNotFoundException("Demande introuvable: " + idDemande));
	}

	// --- METHODES UTILITAIRES PRIVEES ---

	private String genererReferenceDemande() {
		String prefixeDate = LocalDate.now().toString().replace("-", "");
		for (int tentative = 0; tentative < 20; tentative++) {
			int suffixe = ThreadLocalRandom.current().nextInt(1000, 10000);
			String ref = "DEM-" + prefixeDate + "-" + suffixe;
			if (!demandeCreditRepository.findByReferenceDemande(ref).isPresent()) {
				return ref;
			}
		}
		throw new IllegalStateException("Impossible de generer une reference de demande unique.");
	}

	private String genererReferenceCredit() {
		String prefixeDate = LocalDate.now().toString().replace("-", "");
		for (int tentative = 0; tentative < 20; tentative++) {
			int suffixe = ThreadLocalRandom.current().nextInt(1000, 10000);
			String ref = "CRD-" + prefixeDate + "-" + suffixe;
			if (!creditRepository.existsByReferenceCredit(ref)) {
				return ref;
			}
		}
		throw new IllegalStateException("Impossible de generer une reference de credit unique.");
	}
}
