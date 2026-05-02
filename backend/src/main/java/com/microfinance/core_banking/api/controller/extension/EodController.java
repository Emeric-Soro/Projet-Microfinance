package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.dto.request.extension.EodRequestDTO;
import com.microfinance.core_banking.dto.request.extension.RestartEodRequestDTO;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.dto.response.extension.EodExecutionResponseDTO;
import com.microfinance.core_banking.dto.response.extension.EodStepResponseDTO;
import com.microfinance.core_banking.service.extension.EodBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.batch.core.JobExecution;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/eod")
@Tag(name = "Fin de Journée (EOD)", description = "API de gestion de la clôture de journée (End of Day) - exécution, statut, redémarrage")
public class EodController {

    private final EodBatchService eodBatchService;

    public EodController(EodBatchService eodBatchService) {
        this.eodBatchService = eodBatchService;
    }

    @PostMapping("/run")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @Operation(summary = "Exécuter la clôture de journée", description = "Déclenche le traitement batch de fin de journée (EOD) pour la période spécifiée. Les calculs comprennent les intérêts créditeurs et débiteurs, les provisions, les échéances et la ventilation comptable.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exécution EOD démarrée avec succès", content = @Content(schema = @Schema(implementation = EodExecutionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide - la date de fin doit être postérieure ou égale à la date de début", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<?> triggerEod(@Valid @RequestBody EodRequestDTO request) {
        if (request.getDateFin().isBefore(request.getDateDebut())) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                    "error", "La date de fin doit etre posterieure ou egale a la date de debut"
            ));
        }
        JobExecution execution = eodBatchService.runEndOfDay(request.getDateDebut(), request.getDateFin());
        return ResponseEntity.ok(toExecutionDto(execution));
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    @Operation(summary = "Consulter le statut d'une exécution EOD", description = "Retourne le statut détaillé d'une exécution de fin de journée à partir de son identifiant. Inclut l'état du job, le code de sortie et les métriques de chaque étape batch.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statut de l'exécution EOD", content = @Content(schema = @Schema(implementation = EodExecutionResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Exécution EOD non trouvée", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<?> getEodStatus(@RequestParam Long executionId) {
        Optional<JobExecution> executionOpt = eodBatchService.getJobExecutionStatus(executionId);
        if (executionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toExecutionDto(executionOpt.get()));
    }

    @PostMapping("/restart")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @Operation(summary = "Redémarrer une exécution EOD échouée", description = "Permet de redémarrer une clôture de journée qui a échoué. Le job batch reprend depuis l'étape où il s'est arrêté afin d'éviter les doubles traitements.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Redémarrage EOD effectué avec succès", content = @Content(schema = @Schema(implementation = EodExecutionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide - erreur de validation", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Exécution EOD non trouvée", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<?> restartFailedEod(@Valid @RequestBody RestartEodRequestDTO request) {
        JobExecution execution = eodBatchService.restartFailedJob(request.getExecutionId());
        return ResponseEntity.ok(toExecutionDto(execution));
    }

    private EodExecutionResponseDTO toExecutionDto(JobExecution execution) {
        EodExecutionResponseDTO dto = new EodExecutionResponseDTO();
        dto.setExecutionId(execution.getId());
        dto.setJobName(execution.getJobInstance().getJobName());
        dto.setStatus(execution.getStatus().toString());
        dto.setExitCode(execution.getExitStatus().getExitCode());
        dto.setExitDescription(execution.getExitStatus().getExitDescription());
        dto.setStartTime(execution.getStartTime());
        dto.setEndTime(execution.getEndTime());
        dto.setLastUpdated(execution.getLastUpdated());

        if (execution.getStepExecutions() != null) {
            dto.setSteps(execution.getStepExecutions().stream().map(step -> {
                EodStepResponseDTO stepDto = new EodStepResponseDTO();
                stepDto.setStepName(step.getStepName());
                stepDto.setStatus(step.getStatus().toString());
                stepDto.setReadCount(Math.toIntExact(step.getReadCount()));
                stepDto.setWriteCount(Math.toIntExact(step.getWriteCount()));
                stepDto.setCommitCount(Math.toIntExact(step.getCommitCount()));
                stepDto.setRollbackCount(Math.toIntExact(step.getRollbackCount()));
                stepDto.setStartTime(step.getStartTime());
                stepDto.setEndTime(step.getEndTime());
                return stepDto;
            }).toList());
        } else {
            dto.setSteps(Collections.emptyList());
        }

        return dto;
    }
}
