package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.dto.response.extension.SystemAuditLogResponseDTO;
import com.microfinance.core_banking.mapper.extension.SystemAuditLogMapper;
import com.microfinance.core_banking.service.extension.SystemAuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Journaux d'audit", description = "API de gestion des journaux d'audit système")
public class SystemAuditLogController {

    private final SystemAuditLogService auditLogService;
    private final SystemAuditLogMapper auditLogMapper;

    @GetMapping
    @Operation(summary = "Lister tous les journaux d'audit avec pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des journaux retournée avec succès"),
            @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SECURITY_AUDIT_VIEW)")
    public ResponseEntity<Page<SystemAuditLogResponseDTO>> getAllAuditLogs(@ParameterObject Pageable pageable) {
        Page<SystemAuditLogResponseDTO> response = auditLogService.getAuditLogs(pageable).map(auditLogMapper::toDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userName}")
    @Operation(summary = "Lister les journaux d'audit par utilisateur avec pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des journaux retournée avec succès"),
            @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SECURITY_AUDIT_VIEW)")
    public ResponseEntity<Page<SystemAuditLogResponseDTO>> getAuditLogsByUser(
            @PathVariable String userName, 
            @ParameterObject Pageable pageable) {
        Page<SystemAuditLogResponseDTO> response = auditLogService.getAuditLogsByUser(userName, pageable).map(auditLogMapper::toDto);
        return ResponseEntity.ok(response);
    }
}
