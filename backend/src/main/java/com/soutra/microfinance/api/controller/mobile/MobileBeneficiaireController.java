package com.soutra.microfinance.api.controller.mobile;

import com.soutra.microfinance.api.helper.SoutraSecurityHelper;
import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.mobile.MobileBeneficiaireRequestDTO;
import com.soutra.microfinance.dto.response.mobile.MobileBeneficiaireResponseDTO;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.service.mobile.MobileBeneficiaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mobile/beneficiaires")
@Tag(name = "Mobile Beneficiaires", description = "API de gestion des beneficiaires pour l'application mobile")
public class MobileBeneficiaireController {

    private final MobileBeneficiaireService beneficiaireService;

    public MobileBeneficiaireController(MobileBeneficiaireService beneficiaireService) {
        this.beneficiaireService = beneficiaireService;
    }

    @Operation(summary = "Lister les beneficiaires", description = "Retourne la liste des beneficiaires du client connecte.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des beneficiaires retournee avec succes")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_BENEFICIAIRE_LIST", resource = "BENEFICIAIRE")
    public ResponseEntity<List<MobileBeneficiaireResponseDTO>> listerBeneficiaires(Authentication authentication) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Long idClient = utilisateur.getClient().getIdClient();
        return ResponseEntity.ok(beneficiaireService.listerBeneficiaires(idClient));
    }

    @Operation(summary = "Ajouter un beneficiaire", description = "Ajoute un nouveau beneficiaire pour le client connecte.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Beneficiaire ajoute avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_BENEFICIAIRE_CREER", resource = "BENEFICIAIRE")
    public ResponseEntity<MobileBeneficiaireResponseDTO> ajouterBeneficiaire(
            @Valid @RequestBody MobileBeneficiaireRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Long idClient = utilisateur.getClient().getIdClient();

        MobileBeneficiaireResponseDTO response = beneficiaireService.ajouterBeneficiaire(
                idClient,
                requestDTO.getNom(),
                requestDTO.getPrenom(),
                requestDTO.getCompteBeneficiaire(),
                requestDTO.getBanque()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Modifier un beneficiaire", description = "Modifie un beneficiaire existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Beneficiaire modifie avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "404", description = "Beneficiaire introuvable")
    })
    @PutMapping("/{idBeneficiaire}")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_BENEFICIAIRE_MODIFIER", resource = "BENEFICIAIRE")
    public ResponseEntity<MobileBeneficiaireResponseDTO> modifierBeneficiaire(
            @PathVariable Long idBeneficiaire,
            @Valid @RequestBody MobileBeneficiaireRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Long idClient = utilisateur.getClient().getIdClient();

        MobileBeneficiaireResponseDTO response = beneficiaireService.modifierBeneficiaire(
                idBeneficiaire,
                idClient,
                requestDTO.getNom(),
                requestDTO.getPrenom(),
                requestDTO.getCompteBeneficiaire(),
                requestDTO.getBanque()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Supprimer un beneficiaire", description = "Supprime un beneficiaire existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Beneficiaire supprime avec succes"),
            @ApiResponse(responseCode = "404", description = "Beneficiaire introuvable")
    })
    @DeleteMapping("/{idBeneficiaire}")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_BENEFICIAIRE_SUPPRIMER", resource = "BENEFICIAIRE")
    public ResponseEntity<Void> supprimerBeneficiaire(
            @PathVariable Long idBeneficiaire,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Long idClient = utilisateur.getClient().getIdClient();

        beneficiaireService.supprimerBeneficiaire(idBeneficiaire, idClient);
        return ResponseEntity.noContent().build();
    }

}
