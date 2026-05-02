package com.microfinance.core_banking.api.controller.guarantor;

import com.microfinance.core_banking.dto.request.guarantor.GuarantorRequestDTO;
import com.microfinance.core_banking.dto.response.guarantor.GuarantorResponseDTO;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.service.guarantor.GuarantorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/guarantors")
@Tag(name = "Guarantors", description = "API de gestion des garants")
public class GuarantorController {

    private final GuarantorService guarantorService;

    public GuarantorController(GuarantorService guarantorService) {
        this.guarantorService = guarantorService;
    }

    @Operation(
            summary = "Creer un Guarantor",
            description = "Crée un guarantor pour un LoanFacility"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Guarantor créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflit métier", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    public ResponseEntity<GuarantorResponseDTO> creerGuarantor(
            @Valid @RequestBody GuarantorRequestDTO requestDTO) {
        GuarantorResponseDTO response = guarantorService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Lister les guarantors",
            description = "Retourne la liste paginée des guarantors"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des guarantors"),
            @ApiResponse(responseCode = "400", description = "Paramètres de pagination invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    public ResponseEntity<Page<GuarantorResponseDTO>> listerGuarantors(@ParameterObject Pageable pageable) {
        Page<GuarantorResponseDTO> page = guarantorService.getAll(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Obtenir les details d'un Guarantor",
            description = "Retourne les informations d'un Guarantor par identifiant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Détails du Guarantor"),
            @ApiResponse(responseCode = "404", description = "Guarantor introuvable")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    public ResponseEntity<GuarantorResponseDTO> obtenirDetailsGuarantor(@PathVariable Long id) {
        GuarantorResponseDTO response = guarantorService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Mettre à jour un Guarantor",
            description = "Met à jour les informations d'un Guarantor existant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Guarantor mis à jour"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Guarantor introuvable")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    public ResponseEntity<GuarantorResponseDTO> mettreAJourGuarantor(
            @PathVariable Long id,
            @Valid @RequestBody GuarantorRequestDTO requestDTO) {
        GuarantorResponseDTO response = guarantorService.update(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Supprimer un Guarantor",
            description = "Supprime un Guarantor par identifiant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Guarantor supprimé"),
            @ApiResponse(responseCode = "404", description = "Guarantor introuvable")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    public ResponseEntity<Void> supprimerGuarantor(@PathVariable Long id) {
        guarantorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
