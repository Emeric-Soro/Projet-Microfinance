package com.microfinance.core_banking.api.controller.extension;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.dto.response.extension.FeatureInventoryResponseDTO;
import com.microfinance.core_banking.service.extension.FeatureInventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/features")
@Tag(name = "Inventaire Fonctionnel", description = "API d'inventaire et découverte des fonctionnalités système")
public class FeatureInventoryController {

    private final FeatureInventoryService featureInventoryService;

    public FeatureInventoryController(FeatureInventoryService featureInventoryService) {
        this.featureInventoryService = featureInventoryService;
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_FEATURE_VIEW)")
    @Operation(summary = "Obtenir l'inventaire des fonctionnalités", description = "Retourne l'inventaire complet de toutes les fonctionnalités système disponibles avec leur statut")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inventaire retourné avec succès", content = @Content(schema = @Schema(implementation = FeatureInventoryResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<FeatureInventoryResponseDTO> getInventory() {
        return ResponseEntity.ok(featureInventoryService.buildInventory());
    }
}
