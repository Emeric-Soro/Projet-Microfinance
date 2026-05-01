package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.AlerteConformite;
import com.microfinance.core_banking.entity.RapportReglementaire;
import com.microfinance.core_banking.service.extension.ConformiteExtensionService;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
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
@RequestMapping("/api/conformite")
public class ConformiteExtensionController {

    private final ConformiteExtensionService conformiteExtensionService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public ConformiteExtensionController(ConformiteExtensionService conformiteExtensionService, PendingActionSubmissionService pendingActionSubmissionService) {
        this.conformiteExtensionService = conformiteExtensionService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @PostMapping("/alertes")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','COMPLIANCE_MANAGE')")
    @AuditLog(action = "COMPLIANCE_ALERT_CREATE", resource = "ALERTE_CONFORMITE")
    public ResponseEntity<Map<String, Object>> creerAlerte(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_ALERTE_CONFORMITE", "ALERTE_CONFORMITE", null, payload, "Creation alerte conformite soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/alertes")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','COMPLIANCE_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerAlertes() {
        return ResponseEntity.ok(conformiteExtensionService.listerAlertes().stream().map(this::toAlerteMap).toList());
    }

    @PostMapping("/rapports")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','COMPLIANCE_MANAGE')")
    @AuditLog(action = "REGULATORY_REPORT_CREATE", resource = "RAPPORT_REGLEMENTAIRE")
    public ResponseEntity<Map<String, Object>> creerRapport(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_RAPPORT_REGLEMENTAIRE", "RAPPORT_REGLEMENTAIRE", payload.get("codeRapport") == null ? null : payload.get("codeRapport").toString(), payload, "Creation rapport reglementaire soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/rapports")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','COMPLIANCE_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerRapports() {
        return ResponseEntity.ok(conformiteExtensionService.listerRapports().stream().map(this::toRapportMap).toList());
    }

    @PostMapping("/clients/{idClient}/rescan")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','COMPLIANCE_MANAGE')")
    @AuditLog(action = "COMPLIANCE_CLIENT_RESCAN", resource = "ALERTE_CONFORMITE")
    public ResponseEntity<Map<String, Object>> rescannerClient(@PathVariable Long idClient, @RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("RESCAN_CLIENT_CONFORMITE", "ALERTE_CONFORMITE", String.valueOf(idClient), payload, "Rescan client soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @PostMapping("/transactions/{idTransaction}/rescan")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','COMPLIANCE_MANAGE')")
    @AuditLog(action = "COMPLIANCE_TRANSACTION_RESCAN", resource = "ALERTE_CONFORMITE")
    public ResponseEntity<Map<String, Object>> rescannerTransaction(@PathVariable Long idTransaction) {
        ActionEnAttente action = pendingActionSubmissionService.submit("RESCAN_TRANSACTION_CONFORMITE", "ALERTE_CONFORMITE", String.valueOf(idTransaction), Map.of("idTransaction", idTransaction), "Rescan transaction soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @PostMapping("/bic/consultations")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','COMPLIANCE_MANAGE')")
    @AuditLog(action = "COMPLIANCE_BIC_INQUIRY", resource = "RAPPORT_REGLEMENTAIRE")
    public ResponseEntity<Map<String, Object>> consulterBic(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("BIC_CONSULTATION", "RAPPORT_REGLEMENTAIRE", payload.get("codeRapport") == null ? null : payload.get("codeRapport").toString(), payload, "Consultation BIC soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @PostMapping("/rapports/prudentiels")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','COMPLIANCE_MANAGE')")
    @AuditLog(action = "REGULATORY_PRUDENTIAL_REPORT", resource = "RAPPORT_REGLEMENTAIRE")
    public ResponseEntity<Map<String, Object>> genererRapportPrudentiel(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("RAPPORT_PRUDENTIEL", "RAPPORT_REGLEMENTAIRE", payload.get("codeRapport") == null ? null : payload.get("codeRapport").toString(), payload, "Rapport prudentiel soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @PostMapping("/rapports/fiscalite")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','COMPLIANCE_MANAGE')")
    @AuditLog(action = "REGULATORY_FISCAL_REPORT", resource = "RAPPORT_REGLEMENTAIRE")
    public ResponseEntity<Map<String, Object>> genererRapportFiscal(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("RAPPORT_FISCAL", "RAPPORT_REGLEMENTAIRE", payload.get("codeRapport") == null ? null : payload.get("codeRapport").toString(), payload, "Rapport fiscal soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
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

    private Map<String, Object> toAlerteMap(AlerteConformite alerte) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idAlerteConformite", alerte.getIdAlerteConformite());
        response.put("referenceAlerte", alerte.getReferenceAlerte());
        response.put("typeAlerte", alerte.getTypeAlerte());
        response.put("niveauRisque", alerte.getNiveauRisque());
        response.put("statut", alerte.getStatut());
        response.put("resume", alerte.getResume());
        response.put("dateDetection", alerte.getDateDetection());
        return response;
    }

    private Map<String, Object> toRapportMap(RapportReglementaire rapport) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idRapportReglementaire", rapport.getIdRapportReglementaire());
        response.put("codeRapport", rapport.getCodeRapport());
        response.put("typeRapport", rapport.getTypeRapport());
        response.put("periode", rapport.getPeriode());
        response.put("statut", rapport.getStatut());
        response.put("cheminFichier", rapport.getCheminFichier());
        response.put("dateGeneration", rapport.getDateGeneration());
        return response;
    }
}
