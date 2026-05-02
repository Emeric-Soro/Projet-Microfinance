package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.service.extension.DataIntegrityAuditor;
import com.microfinance.core_banking.service.security.SecurityConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class DataIntegrityController {

    private final DataIntegrityAuditor dataIntegrityAuditor;

    public DataIntegrityController(DataIntegrityAuditor dataIntegrityAuditor) {
        this.dataIntegrityAuditor = dataIntegrityAuditor;
    }

    @GetMapping("/integrity/check")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    public ResponseEntity<Map<String, Object>> verifierIntegrite() {
        return ResponseEntity.ok(dataIntegrityAuditor.verifierIntegrite());
    }
}
