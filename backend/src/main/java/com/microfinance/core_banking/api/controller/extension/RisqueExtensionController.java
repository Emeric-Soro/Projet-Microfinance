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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
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
@Tag(name = "Risques", description = "API de gestion des risques, incidents opérationnels, stress tests et liquidité")
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
    @Operation(summary = "Créer un risque", description = "Soumet une demande de création d'un nouveau risque via le workflow de validation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Demande de création de risque soumise et en attente de validation", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit - risque déjà existant", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
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
    @Operation(summary = "Lister les risques", description = "Retourne la liste de tous les risques enregistrés dans le système")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des risques retournée avec succès", content = @Content(schema = @Schema(implementation = RisqueResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<RisqueResponseDTO>> listerRisques() {
        return ResponseEntity.ok(risqueExtensionService.listerRisques().stream().map(this::toDto).toList());
    }

    @PostMapping("/incidents")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_RISK_MANAGE)")
    @AuditLog(action = "RISK_INCIDENT_CREATE", resource = "INCIDENT_OPERATIONNEL")
    @Operation(summary = "Déclarer un incident opérationnel", description = "Soumet une déclaration d'incident opérationnel via le workflow de validation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Déclaration d'incident soumise et en attente de validation", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
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
    @Operation(summary = "Lister les incidents opérationnels", description = "Retourne la liste de tous les incidents opérationnels enregistrés")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des incidents retournée avec succès", content = @Content(schema = @Schema(implementation = IncidentOperationnelResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<IncidentOperationnelResponseDTO>> listerIncidents() {
        return ResponseEntity.ok(risqueExtensionService.listerIncidents().stream().map(this::toDto).toList());
    }

    @PostMapping("/stress-tests")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_RISK_MANAGE)")
    @AuditLog(action = "RISK_STRESS_CREATE", resource = "STRESS_TEST")
    @Operation(summary = "Créer un scénario de stress test", description = "Soumet la création d'un nouveau scénario de stress test via le workflow de validation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Demande de création de stress test soumise et en attente de validation", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit - scénario déjà existant", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
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
    @Operation(summary = "Lister les stress tests", description = "Retourne la liste de tous les scénarios de stress test enregistrés")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des stress tests retournée avec succès", content = @Content(schema = @Schema(implementation = StressTestResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<StressTestResponseDTO>> listerStressTests() {
        return ResponseEntity.ok(risqueExtensionService.listerStressTests().stream().map(this::toDto).toList());
    }

    @PostMapping("/stress-tests/{idStressTest}/executions")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_RISK_MANAGE)")
    @AuditLog(action = "RISK_STRESS_EXECUTE", resource = "RESULTAT_STRESS_TEST")
    @Operation(summary = "Exécuter un stress test", description = "Soumet l'exécution d'un scénario de stress test pour un identifiant donné via le workflow de validation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Exécution du stress test soumise et en attente de validation", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Stress test non trouvé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
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
    @Operation(summary = "Lister les résultats de stress tests", description = "Retourne la liste de tous les résultats d'exécution de stress tests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des résultats retournée avec succès", content = @Content(schema = @Schema(implementation = ResultatStressTestResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<ResultatStressTestResponseDTO>> listerResultats() {
        return ResponseEntity.ok(risqueExtensionService.listerResultatsStressTests().stream().map(this::toDto).toList());
    }

    @GetMapping("/liquidite")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_RISK_VIEW)")
    @Operation(summary = "Consulter le tableau de liquidité", description = "Retourne le tableau de bord de la liquidité calculé à partir des données financières")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tableau de liquidité retourné avec succès", content = @Content(schema = @Schema(implementation = TableauLiquiditeResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
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
