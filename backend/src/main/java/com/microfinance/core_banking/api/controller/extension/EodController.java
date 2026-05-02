package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.dto.request.extension.EodRequestDTO;
import com.microfinance.core_banking.dto.request.extension.RestartEodRequestDTO;
import com.microfinance.core_banking.dto.response.extension.EodExecutionResponseDTO;
import com.microfinance.core_banking.dto.response.extension.EodStepResponseDTO;
import com.microfinance.core_banking.service.extension.EodBatchService;
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
public class EodController {

    private final EodBatchService eodBatchService;

    public EodController(EodBatchService eodBatchService) {
        this.eodBatchService = eodBatchService;
    }

    @PostMapping("/run")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
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
    public ResponseEntity<?> getEodStatus(@RequestParam Long executionId) {
        Optional<JobExecution> executionOpt = eodBatchService.getJobExecutionStatus(executionId);
        if (executionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toExecutionDto(executionOpt.get()));
    }

    @PostMapping("/restart")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
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
