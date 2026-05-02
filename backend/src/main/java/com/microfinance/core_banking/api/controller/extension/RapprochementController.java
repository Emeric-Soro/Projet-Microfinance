package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.RapprochementInterAgenceRequestDTO;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.dto.response.extension.RapprochementResponseDTO;
import com.microfinance.core_banking.entity.RapprochementInterAgence;
import com.microfinance.core_banking.service.extension.RapprochementInterAgenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rapprochement")
@Tag(name = "Rapprochement Inter-Agence", description = "API de gestion des rapprochements inter-agences")
public class RapprochementController {

    private final RapprochementInterAgenceService rapprochementService;

    public RapprochementController(RapprochementInterAgenceService rapprochementService) {
        this.rapprochementService = rapprochementService;
    }

    @Operation(summary = "Créer un rapprochement inter-agence", description = "Crée un nouveau rapprochement entre deux agences pour une période donnée")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Rapprochement créé"),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Agence introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @AuditLog(action = "RAPPROCHEMENT_CREATE", resource = "RAPPROCHEMENT_INTER_AGENCE")
    public ResponseEntity<RapprochementResponseDTO> creerRapprochement(@Valid @RequestBody RapprochementInterAgenceRequestDTO dto) {
        RapprochementInterAgence rapprochement = rapprochementService.creerRapprochement(
                dto.getIdAgenceSource(), dto.getIdAgenceDestination(),
                dto.getPeriodeDebut(), dto.getPeriodeFin(),
                dto.getMontantDebit(), dto.getMontantCredit(),
                dto.getCommentaire());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(rapprochement));
    }

    @Operation(summary = "Valider un rapprochement", description = "Valide un rapprochement inter-agence existant")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rapprochement validé"),
        @ApiResponse(responseCode = "400", description = "Erreur de validation", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Rapprochement introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{id}/validation")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @AuditLog(action = "RAPPROCHEMENT_VALIDATE", resource = "RAPPROCHEMENT_INTER_AGENCE")
    public ResponseEntity<RapprochementResponseDTO> validerRapprochement(@PathVariable Long id) {
        RapprochementInterAgence rapprochement = rapprochementService.validerRapprochement(id, null);
        return ResponseEntity.ok(toDto(rapprochement));
    }

    @Operation(summary = "Rejeter un rapprochement", description = "Rejette un rapprochement inter-agence avec un motif")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rapprochement rejeté"),
        @ApiResponse(responseCode = "400", description = "Erreur de validation", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Rapprochement introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{id}/rejet")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @AuditLog(action = "RAPPROCHEMENT_REJECT", resource = "RAPPROCHEMENT_INTER_AGENCE")
    public ResponseEntity<RapprochementResponseDTO> rejeterRapprochement(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String motif = body.getOrDefault("motif", "Rejet sans motif");
        RapprochementInterAgence rapprochement = rapprochementService.rejeterRapprochement(id, motif);
        return ResponseEntity.ok(toDto(rapprochement));
    }

    @Operation(summary = "Lister les rapprochements", description = "Retourne la liste des rapprochements pour une agence donnée")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des rapprochements"),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<List<RapprochementResponseDTO>> listerRapprochements(
            @RequestParam(required = false) Long idAgence) {
        if (idAgence == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(rapprochementService.listerRapprochements(idAgence).stream()
                .map(this::toDto).toList());
    }

    @Operation(summary = "Consulter un rapprochement", description = "Retourne les détails d'un rapprochement par son identifiant")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rapprochement trouvé"),
        @ApiResponse(responseCode = "404", description = "Rapprochement introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<RapprochementResponseDTO> consulterRapprochement(@PathVariable Long id) {
        return rapprochementService.consulterRapprochement(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private RapprochementResponseDTO toDto(RapprochementInterAgence entity) {
        RapprochementResponseDTO dto = new RapprochementResponseDTO();
        dto.setIdRapprochementInterAgence(entity.getIdRapprochementInterAgence());
        dto.setAgenceSource(entity.getAgenceSource() != null ? entity.getAgenceSource().getLibelle() : null);
        dto.setAgenceDestination(entity.getAgenceDestination() != null ? entity.getAgenceDestination().getLibelle() : null);
        dto.setPeriodeDebut(entity.getPeriodeDebut());
        dto.setPeriodeFin(entity.getPeriodeFin());
        dto.setMontantDebit(entity.getMontantDebit());
        dto.setMontantCredit(entity.getMontantCredit());
        dto.setEcart(entity.getEcart());
        dto.setStatut(entity.getStatut());
        dto.setIdValidateur(entity.getValidateur() != null ? entity.getValidateur().getIdUser() : null);
        dto.setDateRapprochement(entity.getDateRapprochement());
        dto.setCommentaire(entity.getCommentaire());
        return dto;
    }
}
