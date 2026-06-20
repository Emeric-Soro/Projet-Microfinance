package com.soutra.microfinance.api.controller.audit;

import com.soutra.microfinance.dto.response.audit.SystemAuditLogResponseDTO;
import com.soutra.microfinance.entity.SystemAuditLog;
import com.soutra.microfinance.service.audit.SystemAuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit-logs")
@Tag(name = "Audit Logs", description = "API de consultation des logs d'audit")
public class AuditLogController {

    private final SystemAuditLogService systemAuditLogService;

    public AuditLogController(SystemAuditLogService systemAuditLogService) {
        this.systemAuditLogService = systemAuditLogService;
    }

    @Operation(summary = "Consulter tous les logs d'audit", description = "Retourne la liste paginee des logs d'audit")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logs d'audit retournes")
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    public ResponseEntity<Page<SystemAuditLogResponseDTO>> consulterTout(@ParameterObject Pageable pageable) {
        Page<SystemAuditLog> logs = systemAuditLogService.consulterTous(pageable);
        Page<SystemAuditLogResponseDTO> dtoPage = logs.map(this::toResponseDTO);
        return ResponseEntity.ok(dtoPage);
    }

    private SystemAuditLogResponseDTO toResponseDTO(SystemAuditLog log) {
        SystemAuditLogResponseDTO dto = new SystemAuditLogResponseDTO();
        dto.setId(log.getId());
        dto.setAction(log.getAction());
        dto.setResource(log.getRessource());
        dto.setUtilisateur(log.getUtilisateur());
        dto.setAdresseIp(log.getAdresseIp());
        dto.setDateAction(log.getDateAction());
        dto.setStatut(log.getStatut());
        dto.setMessageErreur(log.getMessageErreur());
        dto.setMethode(log.getMethode());
        return dto;
    }
}
