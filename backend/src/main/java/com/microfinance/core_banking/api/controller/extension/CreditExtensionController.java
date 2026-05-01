package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.Credit;
import com.microfinance.core_banking.entity.DemandeCredit;
import com.microfinance.core_banking.entity.EcheanceCredit;
import com.microfinance.core_banking.entity.GarantieCredit;
import com.microfinance.core_banking.entity.ImpayeCredit;
import com.microfinance.core_banking.entity.ProvisionCredit;
import com.microfinance.core_banking.entity.ProduitCredit;
import com.microfinance.core_banking.entity.RemboursementCredit;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/credits")
public class CreditExtensionController {

    private final CreditExtensionService creditExtensionService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public CreditExtensionController(CreditExtensionService creditExtensionService, PendingActionSubmissionService pendingActionSubmissionService) {
        this.creditExtensionService = creditExtensionService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @PostMapping("/produits")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CREDIT_MANAGE')")
    @AuditLog(action = "CREDIT_PRODUCT_CREATE", resource = "PRODUIT_CREDIT")
    public ResponseEntity<Map<String, Object>> creerProduit(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_PRODUIT_CREDIT", "PRODUIT_CREDIT", (String) payload.get("codeProduit"), payload, "Creation produit credit soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/produits")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','CREDIT_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerProduits() {
        return ResponseEntity.ok(creditExtensionService.listerProduits().stream().map(this::toProduitMap).toList());
    }

    @PostMapping("/demandes")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','CREDIT_MANAGE')")
    @AuditLog(action = "CREDIT_REQUEST_CREATE", resource = "DEMANDE_CREDIT")
    public ResponseEntity<Map<String, Object>> creerDemande(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toDemandeMap(creditExtensionService.creerDemande(payload)));
    }

    @GetMapping("/demandes")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','CREDIT_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerDemandes() {
        return ResponseEntity.ok(creditExtensionService.listerDemandes().stream().map(this::toDemandeMap).toList());
    }

    @PutMapping("/demandes/{idDemande}/decision")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CREDIT_MANAGE')")
    @AuditLog(action = "CREDIT_REQUEST_DECISION", resource = "DEMANDE_CREDIT")
    public ResponseEntity<Map<String, Object>> deciderDemande(@PathVariable Long idDemande, @RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("DECISION_DEMANDE_CREDIT", "DEMANDE_CREDIT", String.valueOf(idDemande), payload, "Decision demande credit soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @PostMapping("/demandes/{idDemande}/deblocage")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CREDIT_MANAGE')")
    @AuditLog(action = "CREDIT_DISBURSE", resource = "CREDIT")
    public ResponseEntity<Map<String, Object>> debloquerCredit(@PathVariable Long idDemande, @RequestBody Map<String, Object> payload) {
        payload.put("idDemande", idDemande);
        ActionEnAttente action = pendingActionSubmissionService.submit("DEBLOCAGE_CREDIT", "CREDIT", String.valueOf(idDemande), payload, "Deblocage credit soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','CREDIT_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerCredits() {
        return ResponseEntity.ok(creditExtensionService.listerCredits().stream().map(this::toCreditMap).toList());
    }

    @GetMapping("/{idCredit}/echeances")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','CREDIT_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerEcheances(@PathVariable Long idCredit) {
        return ResponseEntity.ok(creditExtensionService.listerEcheances(idCredit).stream().map(this::toEcheanceMap).toList());
    }

    @PostMapping("/{idCredit}/garanties")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CREDIT_MANAGE')")
    @AuditLog(action = "CREDIT_COLLATERAL_CREATE", resource = "GARANTIE_CREDIT")
    public ResponseEntity<Map<String, Object>> enregistrerGarantie(@PathVariable Long idCredit, @RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_GARANTIE_CREDIT", "GARANTIE_CREDIT", String.valueOf(idCredit), payload, "Creation garantie credit soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/{idCredit}/garanties")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','CREDIT_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerGaranties(@PathVariable Long idCredit) {
        return ResponseEntity.ok(creditExtensionService.listerGaranties(idCredit).stream().map(this::toGarantieMap).toList());
    }

    @PostMapping("/{idCredit}/remboursements")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','CREDIT_MANAGE')")
    @AuditLog(action = "CREDIT_REPAYMENT_CREATE", resource = "REMBOURSEMENT_CREDIT")
    public ResponseEntity<Map<String, Object>> rembourserCredit(@PathVariable Long idCredit, @RequestBody Map<String, Object> payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toRemboursementMap(creditExtensionService.rembourserCredit(idCredit, payload)));
    }

    @GetMapping("/{idCredit}/remboursements")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','CREDIT_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerRemboursements(@PathVariable Long idCredit) {
        return ResponseEntity.ok(creditExtensionService.listerRemboursements(idCredit).stream().map(this::toRemboursementMap).toList());
    }

    @PostMapping("/impayes/detection")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CREDIT_MANAGE')")
    @AuditLog(action = "CREDIT_OVERDUE_DETECT", resource = "IMPAYE_CREDIT")
    public ResponseEntity<List<Map<String, Object>>> detecterImpayes(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("DETECTION_IMPAYE_CREDIT", "IMPAYE_CREDIT", null, payload, "Detection impayes soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(List.of(toActionMap(action)));
    }

    @GetMapping("/{idCredit}/impayes")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','CREDIT_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerImpayes(@PathVariable Long idCredit) {
        return ResponseEntity.ok(creditExtensionService.listerImpayes(idCredit).stream().map(this::toImpayeMap).toList());
    }

    @PostMapping("/provisions/calcul")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CREDIT_MANAGE')")
    @AuditLog(action = "CREDIT_PROVISION_CALCULATE", resource = "PROVISION_CREDIT")
    public ResponseEntity<List<Map<String, Object>>> calculerProvisions(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CALCUL_PROVISION_CREDIT", "PROVISION_CREDIT", null, payload, "Calcul provisions soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(List.of(toActionMap(action)));
    }

    @GetMapping("/{idCredit}/provisions")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','CREDIT_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerProvisions(@PathVariable Long idCredit) {
        return ResponseEntity.ok(creditExtensionService.listerProvisions(idCredit).stream().map(this::toProvisionMap).toList());
    }

    private Map<String, Object> toProduitMap(ProduitCredit produit) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idProduitCredit", produit.getIdProduitCredit());
        response.put("codeProduit", produit.getCodeProduit());
        response.put("libelle", produit.getLibelle());
        response.put("categorie", produit.getCategorie());
        response.put("tauxAnnuel", produit.getTauxAnnuel());
        response.put("montantMin", produit.getMontantMin());
        response.put("montantMax", produit.getMontantMax());
        response.put("statut", produit.getStatut());
        return response;
    }

    private Map<String, Object> toDemandeMap(DemandeCredit demande) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idDemandeCredit", demande.getIdDemandeCredit());
        response.put("referenceDossier", demande.getReferenceDossier());
        response.put("idClient", demande.getClient().getIdClient());
        response.put("produit", demande.getProduitCredit().getLibelle());
        response.put("montantDemande", demande.getMontantDemande());
        response.put("dureeMois", demande.getDureeMois());
        response.put("statut", demande.getStatut());
        response.put("scoreCredit", demande.getScoreCredit());
        return response;
    }

    private Map<String, Object> toCreditMap(Credit credit) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idCredit", credit.getIdCredit());
        response.put("referenceCredit", credit.getReferenceCredit());
        response.put("idClient", credit.getClient().getIdClient());
        response.put("montantAccorde", credit.getMontantAccorde());
        response.put("tauxAnnuel", credit.getTauxAnnuel());
        response.put("mensualite", credit.getMensualite());
        response.put("capitalRestantDu", credit.getCapitalRestantDu());
        response.put("fraisPreleves", credit.getFraisPreleves());
        response.put("referenceTransactionDeblocage", credit.getReferenceTransactionDeblocage());
        response.put("statut", credit.getStatut());
        return response;
    }

    private Map<String, Object> toEcheanceMap(EcheanceCredit echeance) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idEcheanceCredit", echeance.getIdEcheanceCredit());
        response.put("numeroEcheance", echeance.getNumeroEcheance());
        response.put("dateEcheance", echeance.getDateEcheance());
        response.put("capitalPrevu", echeance.getCapitalPrevu());
        response.put("interetPrevu", echeance.getInteretPrevu());
        response.put("assurancePrevue", echeance.getAssurancePrevue());
        response.put("totalPrevu", echeance.getTotalPrevu());
        response.put("capitalPaye", echeance.getCapitalPaye());
        response.put("interetPaye", echeance.getInteretPaye());
        response.put("assurancePayee", echeance.getAssurancePayee());
        response.put("statut", echeance.getStatut());
        return response;
    }

    private Map<String, Object> toGarantieMap(GarantieCredit garantie) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idGarantieCredit", garantie.getIdGarantieCredit());
        response.put("typeGarantie", garantie.getTypeGarantie());
        response.put("description", garantie.getDescription());
        response.put("valeur", garantie.getValeur());
        response.put("valeurNantie", garantie.getValeurNantie());
        response.put("statut", garantie.getStatut());
        return response;
    }

    private Map<String, Object> toRemboursementMap(RemboursementCredit remboursement) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idRemboursementCredit", remboursement.getIdRemboursementCredit());
        response.put("referenceRemboursement", remboursement.getReferenceRemboursement());
        response.put("montant", remboursement.getMontant());
        response.put("capitalPaye", remboursement.getCapitalPaye());
        response.put("interetPaye", remboursement.getInteretPaye());
        response.put("assurancePayee", remboursement.getAssurancePayee());
        response.put("referenceTransaction", remboursement.getReferenceTransaction());
        response.put("datePaiement", remboursement.getDatePaiement());
        response.put("statut", remboursement.getStatut());
        return response;
    }

    private Map<String, Object> toImpayeMap(ImpayeCredit impaye) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idImpayeCredit", impaye.getIdImpayeCredit());
        response.put("idCredit", impaye.getCredit().getIdCredit());
        response.put("idEcheanceCredit", impaye.getEcheanceCredit().getIdEcheanceCredit());
        response.put("montant", impaye.getMontant());
        response.put("joursRetard", impaye.getJoursRetard());
        response.put("classeRisque", impaye.getClasseRisque());
        response.put("statut", impaye.getStatut());
        return response;
    }

    private Map<String, Object> toProvisionMap(ProvisionCredit provision) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idProvisionCredit", provision.getIdProvisionCredit());
        response.put("idCredit", provision.getCredit().getIdCredit());
        response.put("dateCalcul", provision.getDateCalcul());
        response.put("tauxProvision", provision.getTauxProvision());
        response.put("montantProvision", provision.getMontantProvision());
        response.put("referencePieceComptable", provision.getReferencePieceComptable());
        response.put("statut", provision.getStatut());
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
