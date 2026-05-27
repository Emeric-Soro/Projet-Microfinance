package com.soutra.microfinance.api.controller.audit;

import com.soutra.microfinance.entity.SystemAuditLog;
import com.soutra.microfinance.service.audit.SystemAuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final SystemAuditLogService systemAuditLogService;

    public AuditLogController(SystemAuditLogService systemAuditLogService) {
        this.systemAuditLogService = systemAuditLogService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    public ResponseEntity<Page<SystemAuditLog>> consulterTout(Pageable pageable) {
        return ResponseEntity.ok(systemAuditLogService.consulterTous(pageable));
    }
}
