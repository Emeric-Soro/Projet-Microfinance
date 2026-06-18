package com.soutra.microfinance.api.controller.compte;

import com.soutra.microfinance.entity.Beneficiaire;
import com.soutra.microfinance.dto.request.compte.BeneficiaireRequestDTO;
import com.soutra.microfinance.dto.response.compte.BeneficiaireResponseDTO;
import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.service.compte.BeneficiaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficiaires")
@RequiredArgsConstructor
@Validated
public class BeneficiaireController {

    private final BeneficiaireService beneficiaireService;

    @Operation(summary = "Lister les beneficiaires")
    @ApiResponse(responseCode = "200", description = "Liste des beneficiaires")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISEUR', 'AGENT')")
    @AuditLog(action = "BENEFICIARY_LIST", resource = "BENEFICIARY")
    public List<BeneficiaireResponseDTO> lister(
            @RequestParam(value = "clientId", required = false) Long clientId
    ) {
        List<Beneficiaire> list;
        if (clientId != null) {
            list = beneficiaireService.listerParClient(clientId);
        } else {
            list = beneficiaireService.listerTous();
        }
        return list.stream()
                .map(BeneficiaireResponseDTO::fromEntity)
                .toList();
    }

    @Operation(summary = "Creer un beneficiaire pour un client")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Beneficiaire cree"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides ou doublon")
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISEUR', 'AGENT')")
    @AuditLog(action = "BENEFICIARY_CREATE", resource = "BENEFICIARY")
    public ResponseEntity<BeneficiaireResponseDTO> creer(
            @RequestParam("clientId") @NotNull Long clientId,
            @Valid @RequestBody BeneficiaireRequestDTO dto
    ) {
        var entity = beneficiaireService.ajouter(
                clientId, dto.getNom(), dto.getPrenom(), dto.getCompteBeneficiaire(), dto.getBanque()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(BeneficiaireResponseDTO.fromEntity(entity));
    }

    @Operation(summary = "Modifier un beneficiaire")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Beneficiaire modifie"),
            @ApiResponse(responseCode = "404", description = "Beneficiaire introuvable")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISEUR', 'AGENT')")
    @AuditLog(action = "BENEFICIARY_UPDATE", resource = "BENEFICIARY")
    public BeneficiaireResponseDTO modifier(
            @PathVariable("id") @NotNull Long id,
            @RequestParam("clientId") @NotNull Long clientId,
            @Valid @RequestBody BeneficiaireRequestDTO dto
    ) {
        var entity = beneficiaireService.modifier(
                id, clientId, dto.getNom(), dto.getPrenom(), dto.getCompteBeneficiaire(), dto.getBanque()
        );
        return BeneficiaireResponseDTO.fromEntity(entity);
    }

    @Operation(summary = "Supprimer un beneficiaire")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Beneficiaire supprime"),
            @ApiResponse(responseCode = "404", description = "Beneficiaire introuvable")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISEUR', 'AGENT')")
    @AuditLog(action = "BENEFICIARY_DELETE", resource = "BENEFICIARY")
    public ResponseEntity<Void> supprimer(
            @PathVariable("id") @NotNull Long id,
            @RequestParam("clientId") @NotNull Long clientId
    ) {
        beneficiaireService.supprimer(id, clientId);
        return ResponseEntity.noContent().build();
    }
}
