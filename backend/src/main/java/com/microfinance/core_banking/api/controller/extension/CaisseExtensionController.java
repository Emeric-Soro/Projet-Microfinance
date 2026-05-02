package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.BilletageCaisseRequestDTO;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.dto.response.extension.CaisseResponseDTO;
import com.microfinance.core_banking.dto.response.extension.BilletageCaisseResponseDTO;
import com.microfinance.core_banking.entity.BilletageCaisse;
import com.microfinance.core_banking.entity.Caisse;
import com.microfinance.core_banking.service.extension.CaisseExtensionService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/caisses")
@Tag(name = "Caisse", description = "API de gestion des caisses, billetage et arrêtés de caisse")
public class CaisseExtensionController {

    private final CaisseExtensionService caisseExtensionService;

    public CaisseExtensionController(CaisseExtensionService caisseExtensionService) {
        this.caisseExtensionService = caisseExtensionService;
    }

    @Operation(summary = "Lister les caisses", description = "Retourne la liste de toutes les caisses de l'institution")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des caisses"),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<List<CaisseResponseDTO>> listerCaisses() {
        return ResponseEntity.ok(caisseExtensionService.listerCaisses().stream().map(this::toDto).toList());
    }

    @Operation(summary = "Consulter une caisse", description = "Retourne les détails d'une caisse par son identifiant")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Caisse trouvée"),
        @ApiResponse(responseCode = "404", description = "Caisse introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<CaisseResponseDTO> consulterCaisse(@PathVariable Long id) {
        return caisseExtensionService.consulterCaisse(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Enregistrer un billetage", description = "Enregistre le détail des billets et pièces d'une session caisse")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Billetage enregistré"),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Session caisse introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/billetage")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @AuditLog(action = "CAISSE_BILLETAGE_CREATE", resource = "BILLETAGE_CAISSE")
    public ResponseEntity<BilletageCaisseResponseDTO> enregistrerBilletage(@Valid @RequestBody BilletageCaisseRequestDTO dto) {
        BilletageCaisse billetage = caisseExtensionService.enregistrerBilletage(
                dto.getIdSessionCaisse(), dto.getCoupure(), dto.getQuantite(), dto.getTypeBilletage());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(billetage));
    }

    @Operation(summary = "Lister le billetage d'une session", description = "Retourne le détail du billetage pour une session caisse donnée")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste du billetage"),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/sessions/{idSessionCaisse}/billetage")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<List<BilletageCaisseResponseDTO>> listerBilletageParSession(@PathVariable Long idSessionCaisse) {
        return ResponseEntity.ok(caisseExtensionService.listerBilletageParSession(idSessionCaisse).stream()
                .map(this::toDto).toList());
    }

    @Operation(summary = "Générer un arrêté de caisse", description = "Génère l'arrêté de caisse pour une date donnée avec le détail des sessions, soldes et écarts")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Arrêté de caisse généré"),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Caisse introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{idCaisse}/arrete")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<Map<String, Object>> genererArreteCaisse(
            @PathVariable Long idCaisse,
            @RequestParam(required = false) LocalDate dateArrete) {
        return ResponseEntity.ok(caisseExtensionService.genererArreteCaisse(idCaisse, dateArrete));
    }

    private CaisseResponseDTO toDto(Caisse caisse) {
        CaisseResponseDTO dto = new CaisseResponseDTO();
        dto.setIdCaisse(caisse.getIdCaisse());
        dto.setCodeCaisse(caisse.getCodeCaisse());
        dto.setLibelle(caisse.getLibelle());
        dto.setAgence(caisse.getAgence() != null ? caisse.getAgence().getLibelle() : null);
        dto.setGuichet(caisse.getGuichet() != null ? caisse.getGuichet().getLibelle() : null);
        dto.setStatut(caisse.getStatut());
        dto.setSoldeTheorique(caisse.getSoldeTheorique());
        return dto;
    }

    private BilletageCaisseResponseDTO toDto(BilletageCaisse billetage) {
        BilletageCaisseResponseDTO dto = new BilletageCaisseResponseDTO();
        dto.setIdBilletage(billetage.getIdBilletage());
        dto.setIdSessionCaisse(billetage.getSessionCaisse().getIdSessionCaisse());
        dto.setCoupure(billetage.getCoupure());
        dto.setQuantite(billetage.getQuantite());
        dto.setTotal(billetage.getTotal());
        dto.setTypeBilletage(billetage.getTypeBilletage());
        dto.setDateBilletage(billetage.getDateBilletage());
        return dto;
    }
}
