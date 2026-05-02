package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.CreerRisqueRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerStressTestRequestDTO;
import com.microfinance.core_banking.dto.request.extension.DeclarerIncidentRequestDTO;
import com.microfinance.core_banking.dto.response.extension.ActionEnAttenteResponseDTO;
import com.microfinance.core_banking.dto.response.extension.IncidentOperationnelResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ResultatStressTestResponseDTO;
import com.microfinance.core_banking.dto.response.extension.RisqueResponseDTO;
import com.microfinance.core_banking.dto.response.extension.StressTestResponseDTO;
import com.microfinance.core_banking.dto.response.extension.TableauLiquiditeResponseDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.IncidentOperationnel;
import com.microfinance.core_banking.entity.ResultatStressTest;
import com.microfinance.core_banking.entity.Risque;
import com.microfinance.core_banking.entity.StressTest;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import com.microfinance.core_banking.service.extension.RisqueExtensionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_RISK_MANAGE)")
    @AuditLog(action = "RISK_CREATE", resource = "RISQUE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerRisque(@Valid @RequestBody CreerRisqueRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "CREATE_RISQUE",
                "RISQUE",
                dto.getCodeRisque(),
                dto,
                "Creation risque soumise"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_RISK_VIEW)")
    public ResponseEntity<List<RisqueResponseDTO>> listerRisques() {
        return ResponseEntity.ok(risqueExtensionService.listerRisques().stream().map(this::toDto).toList());
    }

    @PostMapping("/incidents")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_RISK_MANAGE)")
    @AuditLog(action = "RISK_INCIDENT_CREATE", resource = "INCIDENT_OPERATIONNEL")
    public ResponseEntity<ActionEnAttenteResponseDTO> declarerIncident(@Valid @RequestBody DeclarerIncidentRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "DECLARE_INCIDENT_OPERATIONNEL",
                "INCIDENT_OPERATIONNEL",
                null,
                dto,
                "Declaration incident operationnel soumise"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/incidents")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_RISK_VIEW)")
    public ResponseEntity<List<IncidentOperationnelResponseDTO>> listerIncidents() {
        return ResponseEntity.ok(risqueExtensionService.listerIncidents().stream().map(this::toDto).toList());
    }

    @PostMapping("/stress-tests")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_RISK_MANAGE)")
    @AuditLog(action = "RISK_STRESS_CREATE", resource = "STRESS_TEST")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerStressTest(@Valid @RequestBody CreerStressTestRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "CREATE_STRESS_TEST",
                "STRESS_TEST",
                dto.getCodeScenario(),
                dto,
                "Creation stress test soumise"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/stress-tests")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_RISK_VIEW)")
    public ResponseEntity<List<StressTestResponseDTO>> listerStressTests() {
        return ResponseEntity.ok(risqueExtensionService.listerStressTests().stream().map(this::toDto).toList());
    }

    @PostMapping("/stress-tests/{idStressTest}/executions")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_RISK_MANAGE)")
    @AuditLog(action = "RISK_STRESS_EXECUTE", resource = "RESULTAT_STRESS_TEST")
    public ResponseEntity<ActionEnAttenteResponseDTO> executerStressTest(@PathVariable Long idStressTest) {
        CreerStressTestRequestDTO dto = new CreerStressTestRequestDTO();
        dto.setDateExecution(String.valueOf(idStressTest));
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "EXECUTE_STRESS_TEST",
                "RESULTAT_STRESS_TEST",
                String.valueOf(idStressTest),
                dto,
                "Execution stress test soumise"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/stress-tests/resultats")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_RISK_VIEW)")
    public ResponseEntity<List<ResultatStressTestResponseDTO>> listerResultats() {
        return ResponseEntity.ok(risqueExtensionService.listerResultatsStressTests().stream().map(this::toDto).toList());
    }

    @GetMapping("/liquidite")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_RISK_VIEW)")
    public ResponseEntity<TableauLiquiditeResponseDTO> tableauLiquidite() {
        return ResponseEntity.ok(risqueExtensionService.calculerTableauLiquidite());
    }

    private RisqueResponseDTO toDto(Risque risque) {
        RisqueResponseDTO dto = new RisqueResponseDTO();
        dto.setIdRisque(risque.getIdRisque());
        dto.setCodeRisque(risque.getCodeRisque());
        dto.setCategorie(risque.getCategorie());
        dto.setLibelle(risque.getLibelle());
        dto.setNiveau(risque.getNiveau());
        dto.setStatut(risque.getStatut());
        return dto;
    }

    private IncidentOperationnelResponseDTO toDto(IncidentOperationnel incident) {
        IncidentOperationnelResponseDTO dto = new IncidentOperationnelResponseDTO();
        dto.setIdIncidentOperationnel(incident.getIdIncidentOperationnel());
        dto.setReferenceIncident(incident.getReferenceIncident());
        dto.setTypeIncident(incident.getTypeIncident());
        dto.setGravite(incident.getGravite());
        dto.setStatut(incident.getStatut());
        dto.setDescription(incident.getDescription());
        dto.setRisque(incident.getRisque() == null ? null : incident.getRisque().getCodeRisque());
        return dto;
    }

    private StressTestResponseDTO toDto(StressTest stressTest) {
        StressTestResponseDTO dto = new StressTestResponseDTO();
        dto.setIdStressTest(stressTest.getIdStressTest());
        dto.setCodeScenario(stressTest.getCodeScenario());
        dto.setLibelle(stressTest.getLibelle());
        dto.setTauxDefaut(stressTest.getTauxDefaut());
        dto.setTauxRetrait(stressTest.getTauxRetrait());
        dto.setStatut(stressTest.getStatut());
        return dto;
    }

    private ResultatStressTestResponseDTO toDto(ResultatStressTest resultat) {
        ResultatStressTestResponseDTO dto = new ResultatStressTestResponseDTO();
        dto.setIdResultatStressTest(resultat.getIdResultatStressTest());
        dto.setStressTest(resultat.getStressTest().getCodeScenario());
        dto.setEncoursCredit(resultat.getEncoursCredit());
        dto.setPertesProjetees(resultat.getPertesProjetees());
        dto.setRetraitsProjetes(resultat.getRetraitsProjetes());
        dto.setLiquiditeNette(resultat.getLiquiditeNette());
        dto.setStatutResultat(resultat.getStatutResultat());
        return dto;
    }

    private ActionEnAttenteResponseDTO toActionDto(ActionEnAttente action) {
        ActionEnAttenteResponseDTO dto = new ActionEnAttenteResponseDTO();
        dto.setIdActionEnAttente(action.getIdActionEnAttente());
        dto.setTypeAction(action.getTypeAction());
        dto.setRessource(action.getRessource());
        dto.setStatut(action.getStatut());
        return dto;
    }
}
