package com.microfinance.core_banking.api.controller.loan;

import com.microfinance.core_banking.dto.request.loan.LoanFacilityRequestDTO;
import com.microfinance.core_banking.dto.response.loan.LoanFacilityResponseDTO;
import com.microfinance.core_banking.service.loan.LoanFacilityService;
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
@RequestMapping("/api/loan-facilities")
@Tag(name = "LoanFacilities", description = "API de gestion des Loan Facilities")
public class LoanFacilityController {

    private final LoanFacilityService loanFacilityService;

    public LoanFacilityController(LoanFacilityService loanFacilityService) {
        this.loanFacilityService = loanFacilityService;
    }

    @Operation(
            summary = "Creer un LoanFacility",
            description = "Crée une facility de prêt à partir des informations fournies"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "LoanFacility créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de création invalides"),
            @ApiResponse(responseCode = "409", description = "Conflit métier")
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    public ResponseEntity<LoanFacilityResponseDTO> creerLoanFacility(
            @Valid @RequestBody LoanFacilityRequestDTO requestDTO) {
        LoanFacilityResponseDTO response = loanFacilityService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Lister les LoanFacilities",
            description = "Retourne la liste paginée des LoanFacilities"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des LoanFacilities"),
            @ApiResponse(responseCode = "400", description = "Paramètres de pagination invalides")
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    public ResponseEntity<Page<LoanFacilityResponseDTO>> listerLoanFacilities(
            @ParameterObject Pageable pageable) {
        Page<LoanFacilityResponseDTO> page = loanFacilityService.getAll(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Obtenir les détails d'un LoanFacility",
            description = "Retourne les informations d'un LoanFacility par identifiant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Détails du LoanFacility"),
            @ApiResponse(responseCode = "404", description = "LoanFacility introuvable")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    public ResponseEntity<LoanFacilityResponseDTO> obtenirDetailsLoanFacility(@PathVariable Long id) {
        LoanFacilityResponseDTO response = loanFacilityService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Mettre à jour un LoanFacility",
            description = "Met à jour les informations d'un LoanFacility existant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "LoanFacility mis à jour"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "LoanFacility introuvable")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    public ResponseEntity<LoanFacilityResponseDTO> mettreAJourLoanFacility(
            @PathVariable Long id,
            @Valid @RequestBody LoanFacilityRequestDTO requestDTO) {
        LoanFacilityResponseDTO response = loanFacilityService.update(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Supprimer un LoanFacility",
            description = "Supprime un LoanFacility par identifiant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "LoanFacility supprimé"),
            @ApiResponse(responseCode = "404", description = "LoanFacility introuvable")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    public ResponseEntity<Void> supprimerLoanFacility(@PathVariable Long id) {
        loanFacilityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
