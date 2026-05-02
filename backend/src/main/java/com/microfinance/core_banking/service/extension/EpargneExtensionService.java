package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CalculerInteretsEpargneRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CloturerDatRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerProduitEpargneServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.SouscrireDatServiceRequestDTO;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.DepotATerme;
import com.microfinance.core_banking.entity.ProduitEpargne;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.extension.DepotATermeRepository;
import com.microfinance.core_banking.repository.extension.ProduitEpargneRepository;
import com.microfinance.core_banking.service.operation.TransactionService;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EpargneExtensionService {

    private final ProduitEpargneRepository produitEpargneRepository;
    private final DepotATermeRepository depotATermeRepository;
    private final ClientRepository clientRepository;
    private final CompteRepository compteRepository;
    private final TransactionService transactionService;
    private final AuthenticatedUserService authenticatedUserService;

    public EpargneExtensionService(
            ProduitEpargneRepository produitEpargneRepository,
            DepotATermeRepository depotATermeRepository,
            ClientRepository clientRepository,
            CompteRepository compteRepository,
            TransactionService transactionService,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.produitEpargneRepository = produitEpargneRepository;
        this.depotATermeRepository = depotATermeRepository;
        this.clientRepository = clientRepository;
        this.compteRepository = compteRepository;
        this.transactionService = transactionService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional
    public ProduitEpargne creerProduit(CreerProduitEpargneServiceRequestDTO dto) {
        ProduitEpargne produit = new ProduitEpargne();
        produit.setCodeProduit(dto.getCodeProduit());
        produit.setLibelle(dto.getLibelle());
        produit.setCategorie(dto.getCategorie());
        produit.setTauxInteret(dto.getTauxInteret());
        produit.setDepotInitialMin(dto.getDepotInitialMin());
        produit.setSoldeMinimum(dto.getSoldeMinimum());
        produit.setFrequenceInteret(dto.getFrequenceInteret());
        produit.setStatut(dto.getStatut());
        return produitEpargneRepository.save(produit);
    }

    @Transactional
    public DepotATerme souscrireDepotATerme(SouscrireDatServiceRequestDTO dto) {
        Client client = clientRepository.findById(dto.getIdClient())
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));
        verifierPerimetreClient(client);
        ProduitEpargne produit = produitEpargneRepository.findById(dto.getIdProduitEpargne())
                .orElseThrow(() -> new EntityNotFoundException("Produit epargne introuvable"));
        Compte compteSupport = compteRepository.findByNumCompte(dto.getNumCompteSupport())
                .orElseThrow(() -> new EntityNotFoundException("Compte support introuvable"));
        if (compteSupport.getClient() == null || compteSupport.getClient().getIdClient() == null || !compteSupport.getClient().getIdClient().equals(client.getIdClient())) {
            throw new IllegalStateException("Le compte support doit appartenir au client du depot a terme");
        }

        DepotATerme depot = new DepotATerme();
        depot.setReferenceDepot("DAT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        depot.setClient(client);
        depot.setProduitEpargne(produit);
        depot.setCompteSupport(compteSupport);
        depot.setMontant(new BigDecimal(dto.getMontant()));
        depot.setDureeMois(Integer.valueOf(dto.getDureeMois()));
        depot.setTauxApplique(dto.getTauxApplique() == null ? produit.getTauxInteret() : dto.getTauxApplique());
        depot.setDateSouscription(dto.getDateSouscription() == null ? LocalDate.now() : LocalDate.parse(dto.getDateSouscription()));
        depot.setDateEcheance(depot.getDateSouscription().plusMonths(depot.getDureeMois()));
        depot.setRenouvellementAuto(dto.getRenouvellementAuto());
        depot.setInteretsEstimes(depot.getMontant()
                .multiply(depot.getTauxApplique())
                .multiply(BigDecimal.valueOf(depot.getDureeMois()))
                .divide(BigDecimal.valueOf(1200), 2, RoundingMode.HALF_UP));
        depot.setStatut(dto.getStatut());
        var transactionSouscription = transactionService.posterRetraitSysteme(
                compteSupport.getNumCompte(),
                depot.getMontant(),
                BigDecimal.ZERO,
                dto.getIdUtilisateurOperateur(),
                "DATSUB-" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase(),
                "DAT_SOUSCRIPTION"
        );
        depot.setReferenceTransactionSouscription(transactionSouscription.getReferenceUnique());
        return depotATermeRepository.save(depot);
    }

    @Transactional
    public List<Compte> calculerInteretsEpargne(CalculerInteretsEpargneRequestDTO dto) {
        LocalDate dateCalcul = dto.getDateCalcul() == null ? LocalDate.now() : LocalDate.parse(dto.getDateCalcul());

        List<Compte> comptesEpargne = compteRepository.findAll().stream()
                .filter(c -> c.getTypeCompte() != null
                        && c.getTypeCompte().getLibelle() != null
                        && ("EPARGNE".equalsIgnoreCase(c.getTypeCompte().getLibelle())
                        || "SAVINGS".equalsIgnoreCase(c.getTypeCompte().getLibelle())))
                .filter(c -> c.getTauxInteret() != null && c.getTauxInteret().compareTo(BigDecimal.ZERO) > 0)
                .filter(c -> c.getSolde() != null && c.getSolde().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        for (Compte compte : comptesEpargne) {
            BigDecimal interets = compte.getSolde()
                    .multiply(compte.getTauxInteret())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (interets.compareTo(BigDecimal.ZERO) > 0) {
                transactionService.posterDepotSysteme(
                        compte.getNumCompte(),
                        interets,
                        BigDecimal.ZERO,
                        dto.getIdUtilisateurOperateur(),
                        "INT-" + compte.getNumCompte() + "-" + dateCalcul.toString().replace("-", ""),
                        "INTERET_EPARGNE"
                );
            }
        }
        return comptesEpargne;
    }

    @Transactional
    public int calculerInteretsCourusMensuels(LocalDate dateCalcul, Long idUtilisateurOperateur) {
        List<Compte> comptesEpargne = compteRepository.findAll().stream()
                .filter(c -> c.getTypeCompte() != null
                        && c.getTypeCompte().getLibelle() != null
                        && ("EPARGNE".equalsIgnoreCase(c.getTypeCompte().getLibelle())
                        || "SAVINGS".equalsIgnoreCase(c.getTypeCompte().getLibelle())))
                .filter(c -> c.getTauxInteret() != null && c.getTauxInteret().compareTo(BigDecimal.ZERO) > 0)
                .filter(c -> c.getSolde() != null && c.getSolde().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        int comptesCredites = 0;
        for (Compte compte : comptesEpargne) {
            BigDecimal interetsMensuels = compte.getSolde()
                    .multiply(compte.getTauxInteret())
                    .divide(BigDecimal.valueOf(12 * 100L), 10, RoundingMode.HALF_UP)
                    .setScale(2, RoundingMode.HALF_UP);

            if (interetsMensuels.compareTo(BigDecimal.ZERO) > 0) {
                transactionService.posterDepotSysteme(
                        compte.getNumCompte(),
                        interetsMensuels,
                        BigDecimal.ZERO,
                        idUtilisateurOperateur,
                        "INTM-" + compte.getNumCompte() + "-" + dateCalcul.toString().replace("-", ""),
                        "INTERET_EPARGNE_MENSUEL"
                );
                comptesCredites++;
            }
        }
        return comptesCredites;
    }

    @Transactional
    public int calculerInteretsEpargne(LocalDate dateCalcul, Long idUtilisateurOperateur) {
        CalculerInteretsEpargneRequestDTO dto = new CalculerInteretsEpargneRequestDTO();
        dto.setDateCalcul(dateCalcul.toString());
        dto.setIdUtilisateurOperateur(idUtilisateurOperateur);
        List<Compte> comptes = calculerInteretsEpargne(dto);
        return comptes.size();
    }

    @Transactional(readOnly = true)
    public Optional<DepotATerme> consulterDepotATerme(Long id) {
        return depotATermeRepository.findById(id);
    }

    @Transactional
    public DepotATerme cloturerDepotATerme(Long id, CloturerDatRequestDTO dto) {
        DepotATerme depot = depotATermeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Depot a terme introuvable: " + id));
        if ("CLOTURE".equalsIgnoreCase(depot.getStatut())) {
            throw new IllegalStateException("Le depot a terme est deja cloture");
        }
        verifierPerimetreClient(depot.getClient());

        boolean estAnticipe = dto.getClotureAnticipee() != null && dto.getClotureAnticipee()
                && LocalDate.now().isBefore(depot.getDateEcheance());

        BigDecimal montantRembourse;
        BigDecimal penalite = BigDecimal.ZERO;

        if (estAnticipe) {
            BigDecimal penaliteTaux = dto.getPenaliteTaux() != null ? dto.getPenaliteTaux() : BigDecimal.valueOf(3.0);
            BigDecimal interetsAcquis = depot.getInteretsEstimes() != null ? depot.getInteretsEstimes() : BigDecimal.ZERO;
            BigDecimal interetsReduits = interetsAcquis.multiply(
                    BigDecimal.valueOf(java.time.temporal.ChronoUnit.DAYS.between(depot.getDateSouscription(), LocalDate.now())))
                    .divide(BigDecimal.valueOf(java.time.temporal.ChronoUnit.DAYS.between(depot.getDateSouscription(), depot.getDateEcheance())), 2, RoundingMode.HALF_UP);
            penalite = depot.getMontant().multiply(penaliteTaux).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            montantRembourse = depot.getMontant().add(interetsReduits).subtract(penalite).max(BigDecimal.ZERO);
        } else {
            montantRembourse = depot.getMontant().add(depot.getInteretsEstimes() != null ? depot.getInteretsEstimes() : BigDecimal.ZERO);
        }

        var transactionCloture = transactionService.posterDepotSysteme(
                depot.getCompteSupport().getNumCompte(),
                montantRembourse,
                penalite,
                dto.getIdUtilisateurOperateur() != null ? dto.getIdUtilisateurOperateur() : authenticatedUserService.getCurrentUserOrThrow().getIdUser(),
                "DATCLO-" + depot.getReferenceDepot(),
                "DAT_CLOTURE"
        );

        depot.setStatut("CLOTURE");
        depot.setReferenceTransactionSouscription(depot.getReferenceTransactionSouscription() + "|CLO-" + transactionCloture.getReferenceUnique());
        return depotATermeRepository.save(depot);
    }

    @Transactional(readOnly = true)
    public List<ProduitEpargne> listerProduits() {
        return produitEpargneRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<DepotATerme> listerDepotsATerme() {
        return depotATermeRepository.findAll().stream()
                .filter(depot -> {
                    if (authenticatedUserService.hasGlobalScope()) {
                        return true;
                    }
                    Long idAgence = authenticatedUserService.getCurrentAgencyId();
                    return depot.getClient() != null
                            && depot.getClient().getAgence() != null
                            && idAgence != null
                            && idAgence.equals(depot.getClient().getAgence().getIdAgence());
                })
                .toList();
    }

    private void verifierPerimetreClient(Client client) {
        if (client == null || client.getAgence() == null) {
            return;
        }
        authenticatedUserService.assertAgencyAccess(client.getAgence().getIdAgence());
    }
}
