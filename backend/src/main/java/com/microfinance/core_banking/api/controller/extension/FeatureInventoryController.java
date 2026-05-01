package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.service.extension.FeatureInventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/features")
public class FeatureInventoryController {

    private final FeatureInventoryService featureInventoryService;

    public FeatureInventoryController(FeatureInventoryService featureInventoryService) {
        this.featureInventoryService = featureInventoryService;
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','FEATURE_VIEW')")
    public ResponseEntity<Map<String, Object>> getInventory() {
        return ResponseEntity.ok(featureInventoryService.buildInventory());
    }
}
