package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.CreerActionRequestDTO;
import com.microfinance.core_banking.dto.request.extension.ValiderActionRequestDTO;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ActionEnAttenteResponseDTO;
import com.microfinance.core_banking.service.extension.ValidationExtensionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/validations")
@Tag(name = "Validations", description = "API de gestion du workflow Maker-Checker (soumission, approbation, rejet des actions en attente)")
public class ValidationExtensionController {

    private final ValidationExtensionService validationExtensionService;

    public ValidationExtensionController(ValidationExtensionService validationExtensionService) {
        this.validationExtensionService = validationExtensionService;
    }

    @PostMapping("/actions-en-attente")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_VALIDATION_VIEW)")
    @AuditLog(action = "PENDING_ACTION_CREATE", resource = "ACTION_EN_ATTENTE")
    @Operation(summary = "Créer une action en attente", description = "Soumet une nouvelle action au workflow Maker-Checker. L'action est créée avec un statut 'EN_ATTENTE' et devra être approuvée ou rejetée par un superviseur.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Action en attente créée avec succès", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide - erreur de validation", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> creerAction(@Valid @RequestBody CreerActionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toActionDto(validationExtensionService.creerAction(dto)));
    }

    @PutMapping("/actions-en-attente/{idAction}/decision")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_VALIDATION_DECIDE)")
    @AuditLog(action = "PENDING_ACTION_DECISION", resource = "ACTION_EN_ATTENTE")
    @Operation(summary = "Valider ou rejeter une action", description = "Permet à un superviseur d'approuver ou de rejeter une action en attente identifiée par son ID. La décision est enregistrée avec le motif fourni dans la requête.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Décision enregistrée avec succès", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide - erreur de validation", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Action en attente non trouvée", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> validerAction(@PathVariable Long idAction, @Valid @RequestBody ValiderActionRequestDTO dto) {
        return ResponseEntity.ok(toActionDto(validationExtensionService.validerAction(idAction, dto)));
    }

    @GetMapping("/actions-en-attente")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_VALIDATION_VIEW)")
    @Operation(summary = "Lister les actions en attente", description = "Retourne la liste complète des actions soumises au workflow Maker-Checker. Chaque action inclut son type, sa ressource cible, sa référence et son statut courant.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des actions en attente", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class)))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<ActionEnAttenteResponseDTO>> listerActions() {
        return ResponseEntity.ok(validationExtensionService.listerActions().stream().map(this::toActionDto).toList());
    }

    private ActionEnAttenteResponseDTO toActionDto(com.microfinance.core_banking.entity.ActionEnAttente action) {
        ActionEnAttenteResponseDTO dto = new ActionEnAttenteResponseDTO();
        dto.setIdActionEnAttente(action.getIdActionEnAttente());
        dto.setTypeAction(action.getTypeAction());
        dto.setRessource(action.getRessource());
        dto.setReferenceRessource(action.getReferenceRessource());
        dto.setStatut(action.getStatut());
        return dto;
    }
}
