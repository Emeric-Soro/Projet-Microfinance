package com.microfinance.core_banking.api.controller.collateral;

import com.microfinance.core_banking.dto.request.collateral.CollateralRequestDTO;
import com.microfinance.core_banking.dto.response.collateral.CollateralResponseDTO;
import com.microfinance.core_banking.service.collateral.CollateralService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/collaterals")
@Tag(name = "Collateral", description = "API de gestion des collatéraux")
public class CollateralController {

    private final CollateralService collateralService;

    public CollateralController(CollateralService collateralService) {
        this.collateralService = collateralService;
    }

    @Operation(
            summary = "Creer un Collateral",
            description = "Crée un collatéral associé à un LoanFacility"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Collateral créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Conflit métier")
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    public ResponseEntity<CollateralResponseDTO> creerCollateral(
            @Valid @RequestBody CollateralRequestDTO requestDTO) {
        CollateralResponseDTO response = collateralService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Lister les collaterals",
            description = "Retourne la liste paginée des collaterals"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des collaterals"),
            @ApiResponse(responseCode = "400", description = "Paramètres de pagination invalides")
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    public ResponseEntity<Page<CollateralResponseDTO>> listerCollaterals(@ParameterObject Pageable pageable) {
        Page<CollateralResponseDTO> page = collateralService.getAll(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Obtenir les détails d'un Collateral",
            description = "Retourne les informations d'un Collateral par identifiant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Détails du Collateral"),
            @ApiResponse(responseCode = "404", description = "Collateral introuvable")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    public ResponseEntity<CollateralResponseDTO> obtenirDetailsCollateral(@PathVariable Long id) {
        CollateralResponseDTO response = collateralService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Mettre à jour un Collateral",
            description = "Met à jour les informations d'un Collateral existant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Collateral mis à jour"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Collateral introuvable")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    public ResponseEntity<CollateralResponseDTO> mettreAJourCollateral(
            @PathVariable Long id,
            @Valid @RequestBody CollateralRequestDTO requestDTO) {
        CollateralResponseDTO response = collateralService.update(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Supprimer un Collateral",
            description = "Supprime un Collateral par identifiant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Collateral supprimé"),
            @ApiResponse(responseCode = "404", description = "Collateral introuvable")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    public ResponseEntity<Void> supprimerCollateral(@PathVariable Long id) {
        collateralService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
