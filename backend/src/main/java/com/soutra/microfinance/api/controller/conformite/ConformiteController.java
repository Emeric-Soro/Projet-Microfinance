package com.soutra.microfinance.api.controller.conformite;

import com.soutra.microfinance.api.helper.SoutraSecurityHelper;
import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.conformite.ConsentementRgpdRequestDTO;
import com.soutra.microfinance.dto.request.conformite.CreateAlerteLcbFtRequestDTO;
import com.soutra.microfinance.dto.request.conformite.CreateRapportSarRequestDTO;
import com.soutra.microfinance.dto.request.conformite.CreateReclamationRequestDTO;
import com.soutra.microfinance.dto.request.conformite.TraiterAlerteLcbFtRequestDTO;
import com.soutra.microfinance.dto.request.conformite.TraiterReclamationRequestDTO;
import com.soutra.microfinance.dto.request.conformite.UpdateSarStatusRequestDTO;
import com.soutra.microfinance.dto.request.conformite.VerifierPepRequestDTO;
import com.soutra.microfinance.dto.response.conformite.AlerteLcbFtResponseDTO;
import com.soutra.microfinance.dto.response.conformite.ConsentementRgpdResponseDTO;
import com.soutra.microfinance.dto.response.conformite.KycExpireResponseDTO;
import com.soutra.microfinance.dto.response.conformite.PepResponseDTO;
import com.soutra.microfinance.dto.response.conformite.RapportSarResponseDTO;
import com.soutra.microfinance.dto.response.conformite.ReclamationResponseDTO;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.service.conformite.ConformiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conformite")
@Tag(name = "Conformite", description = "API de conformite reglementaire (SAR, Reclamations, RGPD, PEP, LCB-FT)")
public class ConformiteController {

    private final ConformiteService conformiteService;

    public ConformiteController(ConformiteService conformiteService) {
        this.conformiteService = conformiteService;
    }

    // ==================== RAPPORTS SAR / CENTIF ====================

    @Operation(
            summary = "Creer un rapport SAR",
            description = "Cree un nouveau rapport d'activite suspecte a transmettre a CENTIF"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rapport SAR cree avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/sar")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "CONFORMITE_CREER_SAR", resource = "CONFORMITE")
    public ResponseEntity<RapportSarResponseDTO> creerRapportSar(
            @Valid @RequestBody CreateRapportSarRequestDTO requestDTO
    ) {
        RapportSarResponseDTO response = conformiteService.creerRapportSar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Lister les rapports SAR",
            description = "Retourne la liste paginee des rapports SAR"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste retournee avec succes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/sar")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "CONFORMITE_LISTER_SAR", resource = "CONFORMITE")
    public ResponseEntity<Page<RapportSarResponseDTO>> listerRapportsSar(
            @ParameterObject Pageable pageable
    ) {
        Page<RapportSarResponseDTO> page = conformiteService.listerRapportsSar(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Obtenir un rapport SAR par ID",
            description = "Retourne les details d'un rapport SAR specifique"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rapport trouve"),
            @ApiResponse(responseCode = "404", description = "Rapport introuvable"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/sar/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "CONFORMITE_GET_SAR", resource = "CONFORMITE")
    public ResponseEntity<RapportSarResponseDTO> getRapportSar(
            @PathVariable Long id
    ) {
        RapportSarResponseDTO response = conformiteService.getRapportSar(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Mettre a jour le statut d'un rapport SAR",
            description = "Met a jour le statut d'un rapport SAR (TRAITE, TRANSMIS, REJETE)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statut mis a jour"),
            @ApiResponse(responseCode = "404", description = "Rapport introuvable"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PutMapping("/sar/{id}/statut")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "CONFORMITE_MAJ_SAR_STATUT", resource = "CONFORMITE")
    public ResponseEntity<RapportSarResponseDTO> mettreAJourStatutSar(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSarStatusRequestDTO requestDTO
    ) {
        RapportSarResponseDTO response = conformiteService.mettreAJourStatutSar(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    // ==================== RECLAMATIONS ====================

    @Operation(
            summary = "Creer une reclamation",
            description = "Cree une nouvelle reclamation client"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reclamation creee avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/reclamations")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "CONFORMITE_CREER_RECLAMATION", resource = "CONFORMITE")
    public ResponseEntity<ReclamationResponseDTO> creerReclamation(
            @Valid @RequestBody CreateReclamationRequestDTO requestDTO
    ) {
        ReclamationResponseDTO response = conformiteService.creerReclamation(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Lister les reclamations",
            description = "Retourne la liste paginee des reclamations"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste retournee avec succes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/reclamations")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "CONFORMITE_LISTER_RECLAMATIONS", resource = "CONFORMITE")
    public ResponseEntity<Page<ReclamationResponseDTO>> listerReclamations(
            @ParameterObject Pageable pageable
    ) {
        Page<ReclamationResponseDTO> page = conformiteService.listerReclamations(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Obtenir une reclamation par ID",
            description = "Retourne les details d'une reclamation specifique"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reclamation trouvee"),
            @ApiResponse(responseCode = "404", description = "Reclamation introuvable"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/reclamations/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "CONFORMITE_GET_RECLAMATION", resource = "CONFORMITE")
    public ResponseEntity<ReclamationResponseDTO> getReclamation(
            @PathVariable Long id
    ) {
        ReclamationResponseDTO response = conformiteService.getReclamation(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Traiter une reclamation",
            description = "Met a jour le statut et traite une reclamation"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reclamation traitee"),
            @ApiResponse(responseCode = "404", description = "Reclamation introuvable"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PutMapping("/reclamations/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "CONFORMITE_TRAITER_RECLAMATION", resource = "CONFORMITE")
    public ResponseEntity<ReclamationResponseDTO> traiterReclamation(
            @PathVariable Long id,
            @Valid @RequestBody TraiterReclamationRequestDTO requestDTO
    ) {
        ReclamationResponseDTO response = conformiteService.traiterReclamation(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    // ==================== RGPD ====================

    @Operation(
            summary = "Enregistrer un consentement RGPD",
            description = "Enregistre le consentement d'un client pour une finalite specifique"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Consentement enregistre"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/rgpd/consentement")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "CONFORMITE_RGPD_CONSENTEMENT", resource = "CONFORMITE")
    public ResponseEntity<Void> enregistrerConsentement(
            @Valid @RequestBody ConsentementRgpdRequestDTO requestDTO
    ) {
        conformiteService.enregistrerConsentement(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Exporter les donnees personnelles",
            description = "Exporte les donnees personnelles d'un client (droit d'acces RGPD)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donnees exportees avec succes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/rgpd/export")
    @PreAuthorize("hasAnyAuthority('ADMIN','CLIENT')")
    @AuditLog(action = "CONFORMITE_RGPD_EXPORT", resource = "CONFORMITE")
    public ResponseEntity<List<ConsentementRgpdResponseDTO>> exporterDonneesPersonnelles(
            @RequestParam Long idClient
    ) {
        verifierProprieteClient(idClient);
        List<ConsentementRgpdResponseDTO> data = conformiteService.exporterDonneesPersonnelles(idClient);
        return ResponseEntity.ok(data);
    }

    @Operation(
            summary = "Effacer les donnees personnelles",
            description = "Efface les donnees personnelles d'un client (droit a l'oubli RGPD)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Donnees effacees"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @DeleteMapping("/rgpd/donnees")
    @PreAuthorize("hasAnyAuthority('ADMIN','CLIENT')")
    @AuditLog(action = "CONFORMITE_RGPD_EFFACEMENT", resource = "CONFORMITE")
    public ResponseEntity<Void> effacerDonnees(
            @RequestParam Long idClient
    ) {
        verifierProprieteClient(idClient);
        conformiteService.effacerDonnees(idClient);
        return ResponseEntity.noContent().build();
    }

    // ==================== KYC EXPIRE ====================

    @Operation(
            summary = "Lister les KYC expires",
            description = "Retourne la liste des KYC non traites depuis plus de 30 jours"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste retournee avec succes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/kyc/expires")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "CONFORMITE_KYC_EXPIRES", resource = "CONFORMITE")
    public ResponseEntity<Page<KycExpireResponseDTO>> listerKycExpires(
            @ParameterObject Pageable pageable
    ) {
        Page<KycExpireResponseDTO> page = conformiteService.listerKycExpires(pageable);
        return ResponseEntity.ok(page);
    }

    // ==================== PEP ====================

    @Operation(
            summary = "Verifier une Personne Politiquement Exposee",
            description = "Enregistre ou met a jour la verification PEP d'un client"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verification enregistree"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/pep/verifier")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "CONFORMITE_VERIFIER_PEP", resource = "CONFORMITE")
    public ResponseEntity<PepResponseDTO> verifierPep(
            @Valid @RequestBody VerifierPepRequestDTO requestDTO
    ) {
        PepResponseDTO response = conformiteService.verifierPep(requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Lister les PEP",
            description = "Retourne la liste de toutes les personnes politiquement exposees"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste retournee avec succes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/pep")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "CONFORMITE_LISTER_PEP", resource = "CONFORMITE")
    public ResponseEntity<Page<PepResponseDTO>> listerPep(
            @ParameterObject Pageable pageable
    ) {
        Page<PepResponseDTO> page = conformiteService.listerPep(pageable);
        return ResponseEntity.ok(page);
    }

    // ==================== ALERTES LCB-FT ====================

    @Operation(
            summary = "Creer une alerte LCB-FT",
            description = "Cree une nouvelle alerte de lutte contre le blanchiment et le financement du terrorisme"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Alerte creee avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/alertes-lcbft")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "CONFORMITE_CREER_ALERTE_LCBFT", resource = "CONFORMITE")
    public ResponseEntity<AlerteLcbFtResponseDTO> creerAlerteLcbFt(
            @Valid @RequestBody CreateAlerteLcbFtRequestDTO requestDTO
    ) {
        AlerteLcbFtResponseDTO response = conformiteService.creerAlerte(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Lister les alertes LCB-FT",
            description = "Retourne la liste de toutes les alertes LCB-FT"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste retournee avec succes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/alertes-lcbft")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "CONFORMITE_LISTER_ALERTES_LCBFT", resource = "CONFORMITE")
    public ResponseEntity<Page<AlerteLcbFtResponseDTO>> listerAlertesLcbFt(
            @ParameterObject Pageable pageable
    ) {
        Page<AlerteLcbFtResponseDTO> page = conformiteService.listerAlertesLcbFt(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Traiter une alerte LCB-FT",
            description = "Met a jour le statut et les actions d'une alerte LCB-FT"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alerte traitee"),
            @ApiResponse(responseCode = "404", description = "Alerte introuvable"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PutMapping("/alertes-lcbft/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "CONFORMITE_TRAITER_ALERTE_LCBFT", resource = "CONFORMITE")
    public ResponseEntity<AlerteLcbFtResponseDTO> traiterAlerteLcbFt(
            @PathVariable Long id,
            @Valid @RequestBody TraiterAlerteLcbFtRequestDTO requestDTO
    ) {
        AlerteLcbFtResponseDTO response = conformiteService.traiterAlerte(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    private void verifierProprieteClient(Long idClient) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        boolean estAdmin = utilisateur.getRoles().stream()
                .anyMatch(r -> "ADMIN".equalsIgnoreCase(r.getCodeRoleUtilisateur()));
        if (estAdmin) {
            return;
        }
        if (utilisateur.getClient() == null || !idClient.equals(utilisateur.getClient().getIdClient())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Acces refuse : vous ne pouvez acceder qu'a vos propres donnees");
        }
    }
}
