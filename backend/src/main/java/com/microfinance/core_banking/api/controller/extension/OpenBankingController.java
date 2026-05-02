package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.ConsentementOpenBanking;
import com.microfinance.core_banking.service.extension.OpenBankingService;
import com.microfinance.core_banking.service.security.SecurityConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/open-banking")
@Tag(name = "Open Banking", description = "API de gestion des consentements Open Banking, partenaires API et webhooks")
public class OpenBankingController {

    private final OpenBankingService openBankingService;

    public OpenBankingController(OpenBankingService openBankingService) {
        this.openBankingService = openBankingService;
    }

    @PostMapping("/consentements")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, "
            + "T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR)")
    @Operation(summary = "Créer un consentement Open Banking", description = "Crée un consentement pour un partenaire API afin d'accéder aux données d'un client")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consentement créé avec succès", content = @Content(schema = @Schema(implementation = ConsentementOpenBanking.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit - consentement déjà existant", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ConsentementOpenBanking> consentir(@RequestBody Map<String, Object> request) {
        Long partenaireId = Long.valueOf(request.get("idPartenaire").toString());
        Long clientId = Long.valueOf(request.get("idClient").toString());
        String type = (String) request.get("typeConsentement");
        String scope = (String) request.get("scope");
        return ResponseEntity.ok(openBankingService.consentir(partenaireId, clientId, type, scope));
    }

    @DeleteMapping("/consentements/{ref}")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, "
            + "T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR)")
    @Operation(summary = "Révoquer un consentement Open Banking", description = "Révoque un consentement existant en indiquant le motif de révocation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Consentement révoqué avec succès", content = @Content),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Consentement non trouvé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> revoguer(@PathVariable String ref, @RequestBody Map<String, String> request) {
        openBankingService.revoguer(ref, request.get("motif"));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/consentements/{ref}/comptes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_VIEW_CLIENT, "
            + "T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    @Operation(summary = "Lister les comptes d'un consentement", description = "Retourne la liste des comptes associés à un consentement Open Banking via sa référence")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des comptes retournée avec succès", content = @Content(schema = @Schema(implementation = Compte.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Consentement non trouvé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<Compte>> listerComptes(@PathVariable String ref) {
        return ResponseEntity.ok(openBankingService.listerComptesClient(ref));
    }

    @GetMapping("/consentements")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    @Operation(summary = "Lister les consentements Open Banking", description = "Retourne la liste des consentements, filtrée par statut ou par identifiant client")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des consentements retournée avec succès", content = @Content(schema = @Schema(implementation = ConsentementOpenBanking.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<ConsentementOpenBanking>> listerConsentements(
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) Long clientId) {
        if (statut != null) {
            return ResponseEntity.ok(openBankingService.listerConsentementsParStatut(statut));
        }
        if (clientId != null) {
            return ResponseEntity.ok(openBankingService.listerConsentementsClient(clientId));
        }
        return ResponseEntity.ok(openBankingService.listerConsentementsParStatut("ACTIF"));
    }
}
