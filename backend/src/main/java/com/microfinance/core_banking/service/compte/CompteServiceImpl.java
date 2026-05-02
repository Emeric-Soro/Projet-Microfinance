package com.microfinance.core_banking.service.compte;

import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.CompteComptable;
import com.microfinance.core_banking.entity.EcritureComptable;
import com.microfinance.core_banking.entity.JournalComptable;
import com.microfinance.core_banking.entity.LigneEcritureComptable;
import com.microfinance.core_banking.entity.SensEcriture;
import com.microfinance.core_banking.entity.StatutClient;
import com.microfinance.core_banking.entity.StatutCompte;
import com.microfinance.core_banking.entity.StatutKycClient;
import com.microfinance.core_banking.entity.TypeCompte;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.compte.StatutCompteRepository;
import com.microfinance.core_banking.repository.compte.TypeCompteRepository;
import com.microfinance.core_banking.repository.extension.EcritureComptableRepository;
import com.microfinance.core_banking.repository.extension.JournalComptableRepository;
import com.microfinance.core_banking.repository.extension.CompteComptableRepository;
import com.microfinance.core_banking.repository.extension.LigneEcritureComptableRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CompteServiceImpl implements CompteService {

    private static final String PREFIXE_COMPTE = "CPT";

    private final CompteRepository compteRepository;
    private final ClientRepository clientRepository;
    private final TypeCompteRepository typeCompteRepository;
    private final StatutCompteRepository statutCompteRepository;
    private final LigneEcritureComptableRepository ligneEcritureComptableRepository;
    private final EcritureComptableRepository ecritureComptableRepository;
    private final JournalComptableRepository journalComptableRepository;
    private final CompteComptableRepository compteComptableRepository;

    public CompteServiceImpl(
            CompteRepository compteRepository,
            ClientRepository clientRepository,
            TypeCompteRepository typeCompteRepository,
            StatutCompteRepository statutCompteRepository,
            LigneEcritureComptableRepository ligneEcritureComptableRepository,
            EcritureComptableRepository ecritureComptableRepository,
            JournalComptableRepository journalComptableRepository,
            CompteComptableRepository compteComptableRepository
    ) {
        this.compteRepository = compteRepository;
        this.clientRepository = clientRepository;
        this.typeCompteRepository = typeCompteRepository;
        this.statutCompteRepository = statutCompteRepository;
        this.ligneEcritureComptableRepository = ligneEcritureComptableRepository;
        this.ecritureComptableRepository = ecritureComptableRepository;
        this.journalComptableRepository = journalComptableRepository;
        this.compteComptableRepository = compteComptableRepository;
    }

    @Override
    @Transactional
    public Compte ouvrirCompte(Long idClient, String codeTypeCompte, BigDecimal depotInitial) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + idClient));

        if (codeTypeCompte == null || codeTypeCompte.isBlank()) {
            throw new IllegalArgumentException("Le type de compte est obligatoire");
        }
        if (depotInitial == null || depotInitial.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le depot initial doit etre strictement positif");
        }
        if (client.getStatutKyc() != StatutKycClient.VALIDE) {
            throw new IllegalStateException("Le client doit disposer d'un dossier KYC valide avant l'ouverture d'un compte");
        }
        if (clientBloque(client.getStatutClient())) {
            throw new IllegalStateException("Impossible d'ouvrir un compte pour un client bloque ou inactif");
        }

        TypeCompte typeCompte = typeCompteRepository.findByLibelleIgnoreCase(codeTypeCompte)
                .orElseThrow(() -> new IllegalStateException("Alerte Système : Le type de compte '" + codeTypeCompte + "' n'est pas configuré."));

        Compte compte = new Compte();
        compte.setNumCompte(genererNumeroCompteUnique(client));
        compte.setClient(client);
        compte.setTypeCompte(typeCompte);
        compte.setAgence(client.getAgence());
        compte.setDateOuverture(LocalDate.now());
        compte.setSolde(depotInitial);
        compte.setDevise("XOF");
        compte.setTauxInteret(BigDecimal.ZERO);
        compte.setDecouvertAutorise(BigDecimal.ZERO);

        Compte compteSauvegarde = compteRepository.save(compte);

        ajouterStatut(compteSauvegarde, "ACTIF");

        // Creer une ecriture comptable pour le depot initial
        JournalComptable journalCaisse = journalComptableRepository.findByCodeJournal("CAI")
                .orElse(null);
        if (journalCaisse != null) {
            EcritureComptable ecriture = new EcritureComptable();
            ecriture.setJournalComptable(journalCaisse);
            ecriture.setDateComptable(LocalDate.now());
            ecriture.setLibelle("Depot initial ouverture compte " + compteSauvegarde.getNumCompte());
            ecriture.setReferencePiece("DINIT-" + compteSauvegarde.getNumCompte());
            ecriture.setStatut("COMPTABILISEE");
            ecriture = ecritureComptableRepository.save(ecriture);

            CompteComptable compte571 = compteComptableRepository.findByNumeroCompte("571000").orElse(null);
            CompteComptable compte251 = compteComptableRepository.findByNumeroCompte("251000").orElse(null);

            if (compte571 != null) {
                LigneEcritureComptable ligneDebit = new LigneEcritureComptable();
                ligneDebit.setEcritureComptable(ecriture);
                ligneDebit.setCompteComptable(compte571);
                ligneDebit.setSens("DEBIT");
                ligneDebit.setMontant(depotInitial);
                ligneDebit.setReferenceAuxiliaire(compteSauvegarde.getNumCompte());
                ligneDebit.setLibelleAuxiliaire(compteSauvegarde.getClient().getNom() + " " + compteSauvegarde.getClient().getPrenom());
                ligneEcritureComptableRepository.save(ligneDebit);
            }

            if (compte251 != null) {
                LigneEcritureComptable ligneCredit = new LigneEcritureComptable();
                ligneCredit.setEcritureComptable(ecriture);
                ligneCredit.setCompteComptable(compte251);
                ligneCredit.setSens("CREDIT");
                ligneCredit.setMontant(depotInitial);
                ligneCredit.setReferenceAuxiliaire(compteSauvegarde.getNumCompte());
                ligneCredit.setLibelleAuxiliaire(compteSauvegarde.getClient().getNom() + " " + compteSauvegarde.getClient().getPrenom());
                ligneEcritureComptableRepository.save(ligneCredit);
            }
        }

        return compteSauvegarde;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal consulterSolde(String numCompte) {
        if (!compteRepository.existsByNumCompte(numCompte)) {
            throw new EntityNotFoundException("Compte introuvable: " + numCompte);
        }
        List<LigneEcritureComptable> lignes = ligneEcritureComptableRepository
                .findByReferenceAuxiliaire(numCompte);
        BigDecimal solde = BigDecimal.ZERO;
        for (LigneEcritureComptable l : lignes) {
            if ("CREDIT".equalsIgnoreCase(l.getSens())) {
                solde = solde.add(l.getMontant() != null ? l.getMontant() : BigDecimal.ZERO);
            } else {
                solde = solde.subtract(l.getMontant() != null ? l.getMontant() : BigDecimal.ZERO);
            }
        }
        // Ajouter le decouvert autorise
        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + numCompte));
        if (compte.getDecouvertAutorise() != null) {
            solde = solde.add(compte.getDecouvertAutorise());
        }
        return solde;
    }

    @Override
    @Transactional
    public Compte changerDecouvertAutorise(String numCompte, BigDecimal nouveauPlafond) {
        if (nouveauPlafond == null || nouveauPlafond.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le découvert autorisé doit être positif ou nul");
        }

        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + numCompte));

        compte.setDecouvertAutorise(nouveauPlafond);
        return compteRepository.save(compte);
    }

    @Override
    @Transactional
    public Compte cloturerCompte(String numCompte) {
        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + numCompte));

        if ("FERME".equals(statutCourant(compte))) {
            return compte;
        }

        if (calculerSoldeGrandLivre(numCompte).compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Impossible de clôturer un compte avec un solde non nul");
        }

        ajouterStatut(compte, "FERME");
        return compte;
    }

    @Override
    @Transactional
    public Compte bloquerCompte(String numCompte, String motif) {
        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + numCompte));
        if ("FERME".equals(statutCourant(compte))) {
            throw new IllegalStateException("Impossible de bloquer un compte deja ferme");
        }
        if (!"BLOQUE".equals(statutCourant(compte))) {
            ajouterStatut(compte, "BLOQUE");
        }
        return compte;
    }

    @Override
    @Transactional
    public Compte debloquerCompte(String numCompte, String motif) {
        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + numCompte));
        if ("FERME".equals(statutCourant(compte))) {
            throw new IllegalStateException("Impossible de debloquer un compte deja ferme");
        }
        if (!"ACTIF".equals(statutCourant(compte))) {
            ajouterStatut(compte, "ACTIF");
        }
        return compte;
    }

    private String genererNumeroCompteUnique(Client client) {
        String date = LocalDate.now().toString().replace("-", "");
        long sequenceDepart = compteRepository.countByClient_IdClient(client.getIdClient()) + 1;

        for (long increment = 0; increment < 100; increment++) {
            long sequence = sequenceDepart + increment;
            String numero = "%s-%s-%06d-%02d".formatted(
                    PREFIXE_COMPTE,
                    date,
                    client.getIdClient(),
                    sequence
            );
            if (!compteRepository.existsByNumCompte(numero)) {
                return numero;
            }
        }

        throw new IllegalStateException("Impossible de generer un numero de compte unique");
    }

    private boolean clientBloque(StatutClient statutClient) {
        if (statutClient == null || statutClient.getLibelleStatut() == null) {
            return false;
        }
        String statut = statutClient.getLibelleStatut().trim().toUpperCase();
        return "BLOQUE".equals(statut) || "SUSPENDU".equals(statut) || "INACTIF".equals(statut);
    }

    private BigDecimal calculerSoldeGrandLivre(String numCompte) {
        List<LigneEcritureComptable> lignes = ligneEcritureComptableRepository.findByReferenceAuxiliaire(numCompte);
        BigDecimal solde = BigDecimal.ZERO;
        for (LigneEcritureComptable ligne : lignes) {
            BigDecimal montant = ligne.getMontant() == null ? BigDecimal.ZERO : ligne.getMontant();
            if ("CREDIT".equalsIgnoreCase(ligne.getSens())) {
                solde = solde.add(montant);
            } else {
                solde = solde.subtract(montant);
            }
        }
        return solde;
    }

    private StatutCompte ajouterStatut(Compte compte, String libelleStatut) {
        StatutCompte statut = new StatutCompte();
        statut.setCompte(compte);
        statut.setLibelleStatut(libelleStatut);
        statut.setDateStatut(LocalDateTime.now());
        StatutCompte sauvegarde = statutCompteRepository.save(statut);
        compte.getStatutsCompte().add(sauvegarde);
        return sauvegarde;
    }

    private String statutCourant(Compte compte) {
        return compte.getStatutsCompte().stream()
                .max(java.util.Comparator.comparing(StatutCompte::getDateStatut, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())))
                .map(StatutCompte::getLibelleStatut)
                .map(String::trim)
                .map(String::toUpperCase)
                .orElse("ACTIF");
    }
}
