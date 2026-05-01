package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.BudgetExploitation;
import com.microfinance.core_banking.entity.BulletinPaie;
import com.microfinance.core_banking.entity.CommandeAchat;
import com.microfinance.core_banking.entity.Fournisseur;
import com.microfinance.core_banking.entity.Immobilisation;
import com.microfinance.core_banking.entity.LigneBudget;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import com.microfinance.core_banking.service.extension.SupportEntrepriseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support")
public class SupportEntrepriseController {

    private final SupportEntrepriseService supportEntrepriseService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public SupportEntrepriseController(SupportEntrepriseService supportEntrepriseService, PendingActionSubmissionService pendingActionSubmissionService) {
        this.supportEntrepriseService = supportEntrepriseService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @PostMapping("/budgets")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','SUPPORT_MANAGE')")
    @AuditLog(action = "SUPPORT_BUDGET_CREATE", resource = "BUDGET")
    public ResponseEntity<Map<String, Object>> creerBudget(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_BUDGET", "BUDGET", payload.get("codeBudget") == null ? null : payload.get("codeBudget").toString(), payload, "Creation budget soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/budgets")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','SUPPORT_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerBudgets() {
        return ResponseEntity.ok(supportEntrepriseService.listerBudgets().stream().map(this::toBudgetMap).toList());
    }

    @GetMapping("/budgets/{idBudget}/lignes")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','SUPPORT_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerLignesBudget(@PathVariable Long idBudget) {
        return ResponseEntity.ok(supportEntrepriseService.listerLignesBudget(idBudget).stream().map(this::toLigneBudgetMap).toList());
    }

    @PostMapping("/fournisseurs")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','SUPPORT_MANAGE')")
    @AuditLog(action = "SUPPORT_SUPPLIER_CREATE", resource = "FOURNISSEUR")
    public ResponseEntity<Map<String, Object>> creerFournisseur(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_FOURNISSEUR", "FOURNISSEUR", payload.get("codeFournisseur") == null ? null : payload.get("codeFournisseur").toString(), payload, "Creation fournisseur soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/fournisseurs")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','SUPPORT_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerFournisseurs() {
        return ResponseEntity.ok(supportEntrepriseService.listerFournisseurs().stream().map(this::toFournisseurMap).toList());
    }

    @PostMapping("/commandes-achat")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','SUPPORT_MANAGE')")
    @AuditLog(action = "SUPPORT_PURCHASE_ORDER_CREATE", resource = "COMMANDE_ACHAT")
    public ResponseEntity<Map<String, Object>> creerCommande(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_COMMANDE_ACHAT", "COMMANDE_ACHAT", payload.get("referenceCommande") == null ? null : payload.get("referenceCommande").toString(), payload, "Creation commande achat soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/commandes-achat")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','SUPPORT_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerCommandes() {
        return ResponseEntity.ok(supportEntrepriseService.listerCommandesAchat().stream().map(this::toCommandeMap).toList());
    }

    @PostMapping("/bulletins-paie")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','SUPPORT_MANAGE')")
    @AuditLog(action = "SUPPORT_PAYROLL_CREATE", resource = "BULLETIN_PAIE")
    public ResponseEntity<Map<String, Object>> genererBulletinPaie(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_BULLETIN_PAIE", "BULLETIN_PAIE", payload.get("periode") == null ? null : payload.get("periode").toString(), payload, "Generation bulletin paie soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/bulletins-paie")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','SUPPORT_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerBulletinsPaie() {
        return ResponseEntity.ok(supportEntrepriseService.listerBulletinsPaie().stream().map(this::toBulletinMap).toList());
    }

    @PostMapping("/immobilisations")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','SUPPORT_MANAGE')")
    @AuditLog(action = "SUPPORT_ASSET_CREATE", resource = "IMMOBILISATION")
    public ResponseEntity<Map<String, Object>> creerImmobilisation(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_IMMOBILISATION", "IMMOBILISATION", payload.get("codeImmobilisation") == null ? null : payload.get("codeImmobilisation").toString(), payload, "Creation immobilisation soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/immobilisations")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','SUPPORT_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerImmobilisations() {
        return ResponseEntity.ok(supportEntrepriseService.listerImmobilisations().stream().map(this::toImmobilisationMap).toList());
    }

    private Map<String, Object> toActionMap(ActionEnAttente action) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idActionEnAttente", action.getIdActionEnAttente());
        response.put("typeAction", action.getTypeAction());
        response.put("ressource", action.getRessource());
        response.put("referenceRessource", action.getReferenceRessource());
        response.put("statut", action.getStatut());
        return response;
    }

    private Map<String, Object> toBudgetMap(BudgetExploitation budget) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idBudget", budget.getIdBudget());
        response.put("codeBudget", budget.getCodeBudget());
        response.put("annee", budget.getAnnee());
        response.put("agence", budget.getAgence() == null ? null : budget.getAgence().getNomAgence());
        response.put("montantTotal", budget.getMontantTotal());
        response.put("statut", budget.getStatut());
        return response;
    }

    private Map<String, Object> toLigneBudgetMap(LigneBudget ligneBudget) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idLigneBudget", ligneBudget.getIdLigneBudget());
        response.put("rubrique", ligneBudget.getRubrique());
        response.put("montantPrevu", ligneBudget.getMontantPrevu());
        response.put("montantEngage", ligneBudget.getMontantEngage());
        response.put("montantConsomme", ligneBudget.getMontantConsomme());
        return response;
    }

    private Map<String, Object> toFournisseurMap(Fournisseur fournisseur) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idFournisseur", fournisseur.getIdFournisseur());
        response.put("codeFournisseur", fournisseur.getCodeFournisseur());
        response.put("nom", fournisseur.getNom());
        response.put("contact", fournisseur.getContact());
        response.put("telephone", fournisseur.getTelephone());
        response.put("email", fournisseur.getEmail());
        response.put("statut", fournisseur.getStatut());
        return response;
    }

    private Map<String, Object> toCommandeMap(CommandeAchat commandeAchat) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idCommandeAchat", commandeAchat.getIdCommandeAchat());
        response.put("referenceCommande", commandeAchat.getReferenceCommande());
        response.put("fournisseur", commandeAchat.getFournisseur().getNom());
        response.put("agence", commandeAchat.getAgence() == null ? null : commandeAchat.getAgence().getNomAgence());
        response.put("objet", commandeAchat.getObjet());
        response.put("montant", commandeAchat.getMontant());
        response.put("dateCommande", commandeAchat.getDateCommande());
        response.put("statut", commandeAchat.getStatut());
        return response;
    }

    private Map<String, Object> toBulletinMap(BulletinPaie bulletinPaie) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idBulletinPaie", bulletinPaie.getIdBulletinPaie());
        response.put("employe", bulletinPaie.getEmploye().getNomComplet());
        response.put("periode", bulletinPaie.getPeriode());
        response.put("salaireBrut", bulletinPaie.getSalaireBrut());
        response.put("retenues", bulletinPaie.getRetenues());
        response.put("salaireNet", bulletinPaie.getSalaireNet());
        response.put("statut", bulletinPaie.getStatut());
        return response;
    }

    private Map<String, Object> toImmobilisationMap(Immobilisation immobilisation) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idImmobilisation", immobilisation.getIdImmobilisation());
        response.put("codeImmobilisation", immobilisation.getCodeImmobilisation());
        response.put("libelle", immobilisation.getLibelle());
        response.put("agence", immobilisation.getAgence() == null ? null : immobilisation.getAgence().getNomAgence());
        response.put("valeurOrigine", immobilisation.getValeurOrigine());
        response.put("dureeAmortissementMois", immobilisation.getDureeAmortissementMois());
        response.put("amortissementMensuel", immobilisation.getAmortissementMensuel());
        response.put("valeurNette", immobilisation.getValeurNette());
        response.put("dateAcquisition", immobilisation.getDateAcquisition());
        response.put("statut", immobilisation.getStatut());
        return response;
    }
}
