package com.soutra.microfinance.api.controller.mobile;

import com.soutra.microfinance.api.helper.SoutraSecurityHelper;
import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.mobile.MiseAJourKycRequestDTO;
import com.soutra.microfinance.dto.request.mobile.MobileDocumentRequestDTO;
import com.soutra.microfinance.dto.request.mobile.ModifierProfilRequestDTO;
import com.soutra.microfinance.dto.response.mobile.MobileKycResponseDTO;
import com.soutra.microfinance.dto.response.mobile.MobileProfilResponseDTO;
import com.soutra.microfinance.entity.Client;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.service.client.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mobile/profil")
@Tag(name = "Mobile Profil", description = "API de gestion du profil pour l'application mobile")
public class MobileProfilController {

    private final ClientService clientService;

    public MobileProfilController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Operation(summary = "Consulter le profil", description = "Retourne les informations du profil du client connecte.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profil retourne avec succes")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_PROFIL_VIEW", resource = "PROFIL")
    public ResponseEntity<MobileProfilResponseDTO> consulterProfil(Authentication authentication) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Client client = utilisateur.getClient();

        return ResponseEntity.ok(toProfilResponse(client));
    }

    @Operation(summary = "Modifier le profil", description = "Modifie les informations du profil du client connecte.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profil modifie avec succes")
    })
    @PutMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_PROFIL_MODIFIER", resource = "PROFIL")
    public ResponseEntity<MobileProfilResponseDTO> modifierProfil(
            @Valid @RequestBody ModifierProfilRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Client client = clientService.modifierProfilMobile(
                utilisateur.getClient().getIdClient(),
                requestDTO.getTelephone(),
                requestDTO.getEmail(),
                requestDTO.getAdresse()
        );

        return ResponseEntity.ok(toProfilResponse(client));
    }

    @Operation(summary = "Consulter le statut KYC", description = "Retourne le statut KYC du client connecte.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statut KYC retourne avec succes")
    })
    @GetMapping("/kyc")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_PROFIL_KYC_VIEW", resource = "PROFIL")
    public ResponseEntity<MobileKycResponseDTO> consulterKyc(Authentication authentication) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Client client = utilisateur.getClient();

        List<String> documentsFournis = new ArrayList<>();
        if (client.getPhotoIdentiteUrl() != null) documentsFournis.add("Photo d'identite");
        if (client.getJustificatifDomicileUrl() != null) documentsFournis.add("Justificatif de domicile");
        if (client.getJustificatifRevenusUrl() != null) documentsFournis.add("Justificatif de revenus");

        MobileKycResponseDTO response = new MobileKycResponseDTO(
                client.getStatutKyc() != null ? client.getStatutKyc().name() : "BROUILLON",
                client.getNiveauRisque() != null ? client.getNiveauRisque().name() : "FAIBLE",
                client.getDateExpirationPieceIdentite(),
                documentsFournis
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Mettre a jour le KYC", description = "Met a jour les informations KYC du client connecte.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "KYC mis a jour avec succes")
    })
    @PutMapping("/kyc")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_PROFIL_KYC_UPDATE", resource = "PROFIL")
    public ResponseEntity<MobileKycResponseDTO> mettreAJourKyc(
            @Valid @RequestBody MiseAJourKycRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        clientService.mettreAJourKycMobile(
                utilisateur.getClient().getIdClient(),
                requestDTO.getProfession(),
                requestDTO.getSecteurActivite(),
                requestDTO.getRevenuMensuel()
        );

        return consulterKyc(authentication);
    }

    @Operation(summary = "Uploader des documents KYC", description = "Upload un document KYC pour le client connecte.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Document uploade avec succes")
    })
    @PostMapping("/documents")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_PROFIL_DOCUMENT_UPLOAD", resource = "PROFIL")
    public ResponseEntity<Void> uploaderDocument(
            @Valid @RequestBody MobileDocumentRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        clientService.enregistrerDocumentKycMobile(
                utilisateur.getClient().getIdClient(),
                requestDTO.getTypeDocument(),
                requestDTO.getNomFichier(),
                requestDTO.getContenuBase64()
        );

        return ResponseEntity.noContent().build();
    }

    private MobileProfilResponseDTO toProfilResponse(Client client) {
        return new MobileProfilResponseDTO(
                client.getIdClient(),
                client.getNom(),
                client.getPrenom(),
                client.getTelephone(),
                client.getEmail(),
                client.getAdresse(),
                client.getProfession(),
                client.getPhotoIdentiteUrl()
        );
    }

}
