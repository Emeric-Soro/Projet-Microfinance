package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.dto.response.extension.FeatureInventoryResponseDTO;
import com.microfinance.core_banking.service.extension.FeatureInventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/features")
public class FeatureInventoryController {

    private final FeatureInventoryService featureInventoryService;

    public FeatureInventoryController(FeatureInventoryService featureInventoryService) {
        this.featureInventoryService = featureInventoryService;
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_FEATURE_VIEW)")
    public ResponseEntity<FeatureInventoryResponseDTO> getInventory() {
        return ResponseEntity.ok(featureInventoryService.buildInventory());
    }
}
