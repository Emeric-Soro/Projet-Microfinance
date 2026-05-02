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
    public DepotATerme cloturerDepotATerme(Long idDepotATerme, CloturerDatRequestDTO dto) {
        DepotATerme depot = depotATermeRepository.findById(idDepotATerme)
                .orElseThrow(() -> new EntityNotFoundException("Depot a terme introuvable"));

        verifierPerimetreClient(depot.getClient());

        if ("CLOTURE".equalsIgnoreCase(depot.getStatut())) {
            throw new IllegalStateException("Le depot a terme est deja cloture");
        }

        BigDecimal montantTotal = depot.getMontant().add(depot.getInteretsEstimes());

        var transaction = transactionService.posterDepotSysteme(
                depot.getCompteSupport().getNumCompte(),
                montantTotal,
                BigDecimal.ZERO,
                dto.getIdUtilisateurOperateur(),
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

    private void verifierPerimetreClient(Client client) {
        if (client == null || client.getAgence() == null) {
            return;
        }
        authenticatedUserService.assertAgencyAccess(client.getAgence().getIdAgence());
    }
}
