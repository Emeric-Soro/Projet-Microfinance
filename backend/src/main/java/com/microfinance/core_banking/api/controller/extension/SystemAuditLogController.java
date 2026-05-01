package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.dto.response.extension.SystemAuditLogResponseDTO;
import com.microfinance.core_banking.mapper.extension.SystemAuditLogMapper;
import com.microfinance.core_banking.service.extension.SystemAuditLogService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Audit Logs", description = "System audit logs management API")
public class SystemAuditLogController {

    private final SystemAuditLogService auditLogService;
    private final SystemAuditLogMapper auditLogMapper;

    @GetMapping
    @Operation(summary = "Get all audit logs with pagination")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECURITY_AUDIT_VIEW')")
    public ResponseEntity<Page<SystemAuditLogResponseDTO>> getAllAuditLogs(@ParameterObject Pageable pageable) {
        Page<SystemAuditLogResponseDTO> response = auditLogService.getAuditLogs(pageable).map(auditLogMapper::toDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userName}")
    @Operation(summary = "Get audit logs by user with pagination")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECURITY_AUDIT_VIEW')")
    public ResponseEntity<Page<SystemAuditLogResponseDTO>> getAuditLogsByUser(
            @PathVariable String userName, 
            @ParameterObject Pageable pageable) {
        Page<SystemAuditLogResponseDTO> response = auditLogService.getAuditLogsByUser(userName, pageable).map(auditLogMapper::toDto);
        return ResponseEntity.ok(response);
    }
}
