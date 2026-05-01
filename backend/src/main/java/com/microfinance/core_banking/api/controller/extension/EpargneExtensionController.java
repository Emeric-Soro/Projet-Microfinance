package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.DepotATerme;
import com.microfinance.core_banking.entity.ProduitEpargne;
import com.microfinance.core_banking.service.extension.EpargneExtensionService;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/epargne")
public class EpargneExtensionController {

    private final EpargneExtensionService epargneExtensionService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public EpargneExtensionController(EpargneExtensionService epargneExtensionService, PendingActionSubmissionService pendingActionSubmissionService) {
        this.epargneExtensionService = epargneExtensionService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @PostMapping("/produits")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','SAVINGS_MANAGE')")
    @AuditLog(action = "SAVINGS_PRODUCT_CREATE", resource = "PRODUIT_EPARGNE")
    public ResponseEntity<Map<String, Object>> creerProduit(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_PRODUIT_EPARGNE", "PRODUIT_EPARGNE", (String) payload.get("codeProduit"), payload, "Creation produit epargne soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/produits")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','SAVINGS_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerProduits() {
        return ResponseEntity.ok(epargneExtensionService.listerProduits().stream().map(this::toProduitMap).toList());
    }

    @PostMapping("/depots-a-terme")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','SAVINGS_MANAGE')")
    @AuditLog(action = "TERM_DEPOSIT_CREATE", resource = "DEPOT_A_TERME")
    public ResponseEntity<Map<String, Object>> souscrireDepotATerme(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toDepotMap(epargneExtensionService.souscrireDepotATerme(payload)));
    }

    @GetMapping("/depots-a-terme")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','SAVINGS_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerDepotsATerme() {
        return ResponseEntity.ok(epargneExtensionService.listerDepotsATerme().stream().map(this::toDepotMap).toList());
    }

    private Map<String, Object> toProduitMap(ProduitEpargne produit) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idProduitEpargne", produit.getIdProduitEpargne());
        response.put("codeProduit", produit.getCodeProduit());
        response.put("libelle", produit.getLibelle());
        response.put("categorie", produit.getCategorie());
        response.put("tauxInteret", produit.getTauxInteret());
        response.put("frequenceInteret", produit.getFrequenceInteret());
        response.put("statut", produit.getStatut());
        return response;
    }

    private Map<String, Object> toDepotMap(DepotATerme depot) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idDepotTerme", depot.getIdDepotTerme());
        response.put("referenceDepot", depot.getReferenceDepot());
        response.put("idClient", depot.getClient().getIdClient());
        response.put("produit", depot.getProduitEpargne().getLibelle());
        response.put("montant", depot.getMontant());
        response.put("tauxApplique", depot.getTauxApplique());
        response.put("interetsEstimes", depot.getInteretsEstimes());
        response.put("dateEcheance", depot.getDateEcheance());
        response.put("compteSupport", depot.getCompteSupport() == null ? null : depot.getCompteSupport().getNumCompte());
        response.put("referenceTransactionSouscription", depot.getReferenceTransactionSouscription());
        response.put("statut", depot.getStatut());
        return response;
    }

    private Map<String, Object> toActionMap(ActionEnAttente action) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idActionEnAttente", action.getIdActionEnAttente());
        response.put("typeAction", action.getTypeAction());
        response.put("ressource", action.getRessource());
        response.put("statut", action.getStatut());
        return response;
    }
}
