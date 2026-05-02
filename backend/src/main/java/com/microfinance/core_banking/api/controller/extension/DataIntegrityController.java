package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.service.extension.DataIntegrityAuditor;
import com.microfinance.core_banking.service.security.SecurityConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Administration Technique", description = "API d'administration technique et de vérification d'intégrité des données")
public class DataIntegrityController {

    private final DataIntegrityAuditor dataIntegrityAuditor;

    public DataIntegrityController(DataIntegrityAuditor dataIntegrityAuditor) {
        this.dataIntegrityAuditor = dataIntegrityAuditor;
    }

    @GetMapping("/integrity/check")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    @Operation(summary = "Vérifier l'intégrité des données", description = "Lance un audit d'intégrité sur l'ensemble des données du système. Vérifie la cohérence des écritures comptables, l'équilibre des soldes et l'intégrité référentielle entre les entités.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rapport d'intégrité généré avec succès", content = @Content(schema = @Schema(implementation = Object.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Map<String, Object>> verifierIntegrite() {
        return ResponseEntity.ok(dataIntegrityAuditor.verifierIntegrite());
    }
}
