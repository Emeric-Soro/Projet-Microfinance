package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.entity.IncidentOperationnel;
import com.microfinance.core_banking.entity.ResultatStressTest;
import com.microfinance.core_banking.entity.Risque;
import com.microfinance.core_banking.entity.StressTest;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import com.microfinance.core_banking.service.extension.RisqueExtensionService;
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
@RequestMapping("/api/risques")
public class RisqueExtensionController {

    private final RisqueExtensionService risqueExtensionService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public RisqueExtensionController(
            RisqueExtensionService risqueExtensionService,
            PendingActionSubmissionService pendingActionSubmissionService
    ) {
        this.risqueExtensionService = risqueExtensionService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','RISK_MANAGE')")
    @AuditLog(action = "RISK_CREATE", resource = "RISQUE")
    public ResponseEntity<Map<String, Object>> creerRisque(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "CREATE_RISQUE",
                "RISQUE",
                payload.get("codeRisque") == null ? null : payload.get("codeRisque").toString(),
                payload,
                "Creation risque soumise"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','RISK_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerRisques() {
        return ResponseEntity.ok(risqueExtensionService.listerRisques().stream().map(this::toRisqueMap).toList());
    }

    @PostMapping("/incidents")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','RISK_MANAGE')")
    @AuditLog(action = "RISK_INCIDENT_CREATE", resource = "INCIDENT_OPERATIONNEL")
    public ResponseEntity<Map<String, Object>> declarerIncident(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "DECLARE_INCIDENT_OPERATIONNEL",
                "INCIDENT_OPERATIONNEL",
                payload.get("referenceIncident") == null ? null : payload.get("referenceIncident").toString(),
                payload,
                "Declaration incident operationnel soumise"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/incidents")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','RISK_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerIncidents() {
        return ResponseEntity.ok(risqueExtensionService.listerIncidents().stream().map(this::toIncidentMap).toList());
    }

    @PostMapping("/stress-tests")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','RISK_MANAGE')")
    @AuditLog(action = "RISK_STRESS_CREATE", resource = "STRESS_TEST")
    public ResponseEntity<Map<String, Object>> creerStressTest(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "CREATE_STRESS_TEST",
                "STRESS_TEST",
                payload.get("codeScenario") == null ? null : payload.get("codeScenario").toString(),
                payload,
                "Creation stress test soumise"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/stress-tests")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','RISK_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerStressTests() {
        return ResponseEntity.ok(risqueExtensionService.listerStressTests().stream().map(this::toStressTestMap).toList());
    }

    @PostMapping("/stress-tests/{idStressTest}/executions")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','RISK_MANAGE')")
    @AuditLog(action = "RISK_STRESS_EXECUTE", resource = "RESULTAT_STRESS_TEST")
    public ResponseEntity<Map<String, Object>> executerStressTest(@PathVariable Long idStressTest) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "EXECUTE_STRESS_TEST",
                "RESULTAT_STRESS_TEST",
                String.valueOf(idStressTest),
                Map.of("idStressTest", idStressTest),
                "Execution stress test soumise"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/stress-tests/resultats")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','RISK_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerResultats() {
        return ResponseEntity.ok(risqueExtensionService.listerResultatsStressTests().stream().map(this::toResultatMap).toList());
    }

    @GetMapping("/liquidite")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','RISK_VIEW')")
    public ResponseEntity<Map<String, Object>> tableauLiquidite() {
        return ResponseEntity.ok(risqueExtensionService.calculerTableauLiquidite());
    }

    private Map<String, Object> toRisqueMap(Risque risque) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idRisque", risque.getIdRisque());
        response.put("codeRisque", risque.getCodeRisque());
        response.put("categorie", risque.getCategorie());
        response.put("libelle", risque.getLibelle());
        response.put("niveau", risque.getNiveau());
        response.put("statut", risque.getStatut());
        return response;
    }

    private Map<String, Object> toIncidentMap(IncidentOperationnel incident) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idIncidentOperationnel", incident.getIdIncidentOperationnel());
        response.put("referenceIncident", incident.getReferenceIncident());
        response.put("typeIncident", incident.getTypeIncident());
        response.put("gravite", incident.getGravite());
        response.put("statut", incident.getStatut());
        response.put("description", incident.getDescription());
        response.put("risque", incident.getRisque() == null ? null : incident.getRisque().getCodeRisque());
        return response;
    }

    private Map<String, Object> toStressTestMap(StressTest stressTest) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idStressTest", stressTest.getIdStressTest());
        response.put("codeScenario", stressTest.getCodeScenario());
        response.put("libelle", stressTest.getLibelle());
        response.put("tauxDefaut", stressTest.getTauxDefaut());
        response.put("tauxRetrait", stressTest.getTauxRetrait());
        response.put("statut", stressTest.getStatut());
        return response;
    }

    private Map<String, Object> toResultatMap(ResultatStressTest resultat) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idResultatStressTest", resultat.getIdResultatStressTest());
        response.put("stressTest", resultat.getStressTest().getCodeScenario());
        response.put("encoursCredit", resultat.getEncoursCredit());
        response.put("pertesProjetees", resultat.getPertesProjetees());
        response.put("retraitsProjetes", resultat.getRetraitsProjetes());
        response.put("liquiditeNette", resultat.getLiquiditeNette());
        response.put("statutResultat", resultat.getStatutResultat());
        return response;
    }

    private Map<String, Object> toActionMap(ActionEnAttente action) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idActionEnAttente", action.getIdActionEnAttente());
        response.put("typeAction", action.getTypeAction());
        response.put("ressource", action.getRessource());
        response.put("statut", action.getStatut());
        response.put("referenceRessource", action.getReferenceRessource());
        return response;
    }
}
