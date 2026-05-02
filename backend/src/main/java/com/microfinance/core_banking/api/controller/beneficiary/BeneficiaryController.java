package com.microfinance.core_banking.api.controller.beneficiary;

import com.microfinance.core_banking.dto.request.beneficiary.BeneficiaryRequestDTO;
import com.microfinance.core_banking.dto.response.beneficiary.BeneficiaryResponseDTO;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.service.beneficiary.BeneficiaryService;
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
@RequestMapping("/api/beneficiaries")
@Tag(name = "Beneficiaries", description = "API de gestion des beneficiaires")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    @Operation(
            summary = "Creer un Beneficiary",
            description = "Crée un bénéficiaire lié à un LoanFacility"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Beneficiary créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflit métier", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    public ResponseEntity<BeneficiaryResponseDTO> creerBeneficiaire(
            @Valid @RequestBody BeneficiaryRequestDTO requestDTO) {
        BeneficiaryResponseDTO response = beneficiaryService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Lister les beneficiaires",
            description = "Retourne la liste paginée des beneficiaires"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des beneficiaires"),
            @ApiResponse(responseCode = "400", description = "Paramètres de pagination invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    public ResponseEntity<Page<BeneficiaryResponseDTO>> listerBeneficiaires(@ParameterObject Pageable pageable) {
        Page<BeneficiaryResponseDTO> page = beneficiaryService.getAll(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Obtenir les details d'un Beneficiary",
            description = "Retourne les informations d'un Beneficiary par identifiant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Détails du Beneficiary"),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Beneficiary introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    public ResponseEntity<BeneficiaryResponseDTO> obtenirDetailsBeneficiaire(@PathVariable Long id) {
        BeneficiaryResponseDTO response = beneficiaryService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Mettre à jour un Beneficiary",
            description = "Met à jour les informations d'un Beneficiary existant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Beneficiary mis à jour"),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Beneficiary introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    public ResponseEntity<BeneficiaryResponseDTO> mettreAJourBeneficiaire(
            @PathVariable Long id,
            @Valid @RequestBody BeneficiaryRequestDTO requestDTO) {
        BeneficiaryResponseDTO response = beneficiaryService.update(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Supprimer un Beneficiary",
            description = "Supprime un Beneficiary par identifiant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Beneficiary supprimé"),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Beneficiary introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    public ResponseEntity<Void> supprimerBeneficiaire(@PathVariable Long id) {
        beneficiaryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
