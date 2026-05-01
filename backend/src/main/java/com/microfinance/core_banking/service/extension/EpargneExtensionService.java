package com.microfinance.core_banking.service.extension;

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
import java.util.Map;
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
    public ProduitEpargne creerProduit(Map<String, Object> payload) {
        ProduitEpargne produit = new ProduitEpargne();
        produit.setCodeProduit(required(payload, "codeProduit"));
        produit.setLibelle(required(payload, "libelle"));
        produit.setCategorie(required(payload, "categorie"));
        produit.setTauxInteret(decimalOrZero(payload, "tauxInteret"));
        produit.setDepotInitialMin(decimalOrZero(payload, "depotInitialMin"));
        produit.setSoldeMinimum(decimalOrZero(payload, "soldeMinimum"));
        produit.setFrequenceInteret((String) payload.get("frequenceInteret"));
        produit.setStatut(defaulted(payload, "statut", "ACTIF"));
        return produitEpargneRepository.save(produit);
    }

    @Transactional
    public DepotATerme souscrireDepotATerme(Map<String, Object> payload) {
        Client client = clientRepository.findById(Long.valueOf(required(payload, "idClient")))
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));
        verifierPerimetreClient(client);
        ProduitEpargne produit = produitEpargneRepository.findById(Long.valueOf(required(payload, "idProduitEpargne")))
                .orElseThrow(() -> new EntityNotFoundException("Produit epargne introuvable"));
        String numCompteSupport = required(payload, "numCompteSupport");
        Compte compteSupport = compteRepository.findByNumCompte(numCompteSupport)
                .orElseThrow(() -> new EntityNotFoundException("Compte support introuvable"));
        if (compteSupport.getClient() == null || compteSupport.getClient().getIdClient() == null || !compteSupport.getClient().getIdClient().equals(client.getIdClient())) {
            throw new IllegalStateException("Le compte support doit appartenir au client du depot a terme");
        }
        Long idUtilisateurOperateur = payload.get("idUtilisateurOperateur") == null
                ? authenticatedUserService.getCurrentUserOrThrow().getIdUser()
                : Long.valueOf(payload.get("idUtilisateurOperateur").toString());

        DepotATerme depot = new DepotATerme();
        depot.setReferenceDepot("DAT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        depot.setClient(client);
        depot.setProduitEpargne(produit);
        depot.setCompteSupport(compteSupport);
        depot.setMontant(new BigDecimal(required(payload, "montant")));
        depot.setDureeMois(Integer.valueOf(required(payload, "dureeMois")));
        depot.setTauxApplique(payload.get("tauxApplique") == null ? produit.getTauxInteret() : new BigDecimal(payload.get("tauxApplique").toString()));
        depot.setDateSouscription(payload.get("dateSouscription") == null ? LocalDate.now() : LocalDate.parse(payload.get("dateSouscription").toString()));
        depot.setDateEcheance(depot.getDateSouscription().plusMonths(depot.getDureeMois()));
        depot.setRenouvellementAuto(Boolean.parseBoolean(String.valueOf(payload.getOrDefault("renouvellementAuto", false))));
        depot.setInteretsEstimes(depot.getMontant()
                .multiply(depot.getTauxApplique())
                .multiply(BigDecimal.valueOf(depot.getDureeMois()))
                .divide(BigDecimal.valueOf(1200), 2, RoundingMode.HALF_UP));
        depot.setStatut(defaulted(payload, "statut", "ACTIF"));
        var transactionSouscription = transactionService.posterRetraitSysteme(
                compteSupport.getNumCompte(),
                depot.getMontant(),
                BigDecimal.ZERO,
                idUtilisateurOperateur,
                "DATSUB-" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase(),
                "DAT_SOUSCRIPTION"
        );
        depot.setReferenceTransactionSouscription(transactionSouscription.getReferenceUnique());
        return depotATermeRepository.save(depot);
    }

    @Transactional
    public List<Compte> calculerInteretsEpargne(Map<String, Object> payload) {
        LocalDate dateCalcul = payload.get("dateCalcul") == null ? LocalDate.now() : LocalDate.parse(payload.get("dateCalcul").toString());
        Long idUtilisateurOperateur = payload.get("idUtilisateurOperateur") == null
                ? authenticatedUserService.getCurrentUserOrThrow().getIdUser()
                : Long.valueOf(payload.get("idUtilisateurOperateur").toString());

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
                        idUtilisateurOperateur,
                        "INT-" + compte.getNumCompte() + "-" + dateCalcul.toString().replace("-", ""),
                        "INTERET_EPARGNE"
                );
            }
        }
        return comptesEpargne;
    }

    @Transactional
    public DepotATerme cloturerDepotATerme(Long idDepotATerme, Map<String, Object> payload) {
        DepotATerme depot = depotATermeRepository.findById(idDepotATerme)
                .orElseThrow(() -> new EntityNotFoundException("Depot a terme introuvable"));
        
        verifierPerimetreClient(depot.getClient());
        
        if ("CLOTURE".equalsIgnoreCase(depot.getStatut())) {
            throw new IllegalStateException("Le depot a terme est deja cloture");
        }

        Long idUtilisateurOperateur = payload.get("idUtilisateurOperateur") == null
                ? authenticatedUserService.getCurrentUserOrThrow().getIdUser()
                : Long.valueOf(payload.get("idUtilisateurOperateur").toString());

        BigDecimal montantTotal = depot.getMontant().add(depot.getInteretsEstimes());
        
        var transaction = transactionService.posterDepotSysteme(
                depot.getCompteSupport().getNumCompte(),
                montantTotal,
                BigDecimal.ZERO,
                idUtilisateurOperateur,
                "DATCLO-" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase(),
                "DAT_CLOTURE"
        );

        depot.setStatut("CLOTURE");
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

    private String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    private BigDecimal decimalOrZero(Map<String, Object> payload, String key) {
        return payload.get(key) == null ? BigDecimal.ZERO : new BigDecimal(payload.get(key).toString());
    }

    private String defaulted(Map<String, Object> payload, String key, String defaultValue) {
        Object value = payload.get(key);
        return value == null || value.toString().isBlank() ? defaultValue : value.toString().trim();
    }

    private void verifierPerimetreClient(Client client) {
        if (client == null || client.getAgence() == null) {
            return;
        }
        authenticatedUserService.assertAgencyAccess(client.getAgence().getIdAgence());
    }
}
