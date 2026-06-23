package com.soutra.microfinance.api.controller.mobile;

import com.soutra.microfinance.api.helper.ApiEnvelope;
import com.soutra.microfinance.api.helper.SoutraSecurityHelper;
import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.mobile.MobileReclamationRequestDTO;
import com.soutra.microfinance.dto.response.mobile.MobileReclamationResponseDTO;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.entity.conformite.Reclamation;
import com.soutra.microfinance.service.mobile.MobileReclamationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mobile/reclamations")
@Tag(name = "Mobile Reclamations", description = "API de gestion des reclamations pour l'application mobile")
public class MobileReclamationController {

    private final MobileReclamationService reclamationService;

    public MobileReclamationController(MobileReclamationService reclamationService) {
        this.reclamationService = reclamationService;
    }

    @Operation(summary = "Lister les reclamations", description = "Retourne la liste des reclamations du client connecte.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des reclamations retournee avec succes")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_RECLAMATION_LIST", resource = "RECLAMATION")
    public ResponseEntity<ApiEnvelope<List<MobileReclamationResponseDTO>>> listerReclamations(Authentication authentication) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Long idClient = utilisateur.getClient().getIdClient();

        List<Reclamation> reclamations = reclamationService.listerReclamations(idClient);
        List<MobileReclamationResponseDTO> response = reclamations.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(ApiEnvelope.success(response));
    }

    @Operation(summary = "Creer une reclamation", description = "Cree une nouvelle reclamation pour le client connecte.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reclamation creee avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_RECLAMATION_CREER", resource = "RECLAMATION")
    public ResponseEntity<MobileReclamationResponseDTO> creerReclamation(
            @Valid @RequestBody MobileReclamationRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Long idClient = utilisateur.getClient().getIdClient();

        Reclamation saved = reclamationService.creerReclamation(idClient, utilisateur.getLogin(), requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @Operation(summary = "Detail d'une reclamation", description = "Retourne le detail d'une reclamation du client connecte.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detail de la reclamation retourne avec succes"),
            @ApiResponse(responseCode = "404", description = "Reclamation introuvable")
    })
    @GetMapping("/{idReclamation}")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_RECLAMATION_DETAIL", resource = "RECLAMATION")
    public ResponseEntity<MobileReclamationResponseDTO> detailReclamation(
            @PathVariable Long idReclamation,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Long idClient = utilisateur.getClient().getIdClient();

        Reclamation reclamation = reclamationService.consulterReclamation(idReclamation, idClient);

        return ResponseEntity.ok(toResponse(reclamation));
    }

    private MobileReclamationResponseDTO toResponse(Reclamation reclamation) {
        return new MobileReclamationResponseDTO(
                reclamation.getIdReclamation(),
                reclamation.getReference(),
                reclamation.getTypeReclamation(),
                reclamation.getDescription(),
                reclamation.getStatut(),
                reclamation.getPriorite(),
                reclamation.getDateCreation(),
                reclamation.getDateTraitement(),
                reclamation.getMotifCloture()
        );
    }

}
