package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.CreerCritereScoringRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerGrilleScoringRequestDTO;
import com.microfinance.core_banking.dto.request.extension.ExecuterScoringRequestDTO;
import com.microfinance.core_banking.dto.response.extension.ActionEnAttenteResponseDTO;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.CritereScoring;
import com.microfinance.core_banking.entity.GrilleScoring;
import com.microfinance.core_banking.entity.ResultatScoring;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import com.microfinance.core_banking.service.extension.ScoringExtensionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scoring")
@Tag(name = "Scoring", description = "API de gestion du scoring credit")
public class ScoringExtensionController {

    private final ScoringExtensionService scoringExtensionService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public ScoringExtensionController(ScoringExtensionService scoringExtensionService, PendingActionSubmissionService pendingActionSubmissionService) {
        this.scoringExtensionService = scoringExtensionService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @Operation(summary = "Créer une grille de scoring", description = "Crée une nouvelle grille de scoring définissant les seuils d'approbation et de rejet. Transit par le workflow Maker-Checker pour validation avant activation.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Création grille scoring soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/grilles")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "SCORING_GRILLE_CREATE", resource = "GRILLE_SCORING")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerGrille(@Valid @RequestBody CreerGrilleScoringRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_GRILLE_SCORING", "GRILLE_SCORING", dto.getCodeGrille(), dto, "Creation grille scoring soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Lister les grilles de scoring", description = "Retourne la liste de toutes les grilles de scoring enregistrées, actives et inactives.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des grilles retournée avec succès", content = @Content(schema = @Schema(implementation = GrilleScoring.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/grilles")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<List<GrilleScoring>> listerGrilles() {
        return ResponseEntity.ok(scoringExtensionService.listerGrilles());
    }

    @Operation(summary = "Créer un critère de scoring", description = "Crée un nouveau critère de scoring (âge, revenu, etc.) utilisé dans les grilles. Transit par le workflow Maker-Checker pour validation.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Création critère scoring soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/criteres")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "SCORING_CRITERE_CREATE", resource = "CRITERE_SCORING")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerCritere(@Valid @RequestBody CreerCritereScoringRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_CRITERE_SCORING", "CRITERE_SCORING", dto.getCodeCritere(), dto, "Creation critere scoring soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Lister les critères de scoring", description = "Retourne la liste de tous les critères de scoring configurés dans le système.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des critères retournée avec succès", content = @Content(schema = @Schema(implementation = CritereScoring.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/criteres")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<List<CritereScoring>> listerCriteres() {
        return ResponseEntity.ok(scoringExtensionService.listerCriteres());
    }

    @Operation(summary = "Exécuter un scoring", description = "Lance le calcul du scoring pour une demande de crédit selon une grille définie. Transit par le workflow Maker-Checker pour validation. Le score calcule automatiquement la décision (APPROUVE, REJETE ou MANUEL) en fonction des seuils de la grille.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Exécution scoring soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Demande ou grille introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/executer")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "SCORING_EXECUTE", resource = "RESULTAT_SCORING")
    public ResponseEntity<ActionEnAttenteResponseDTO> executerScoring(@Valid @RequestBody ExecuterScoringRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("EXECUTE_SCORING", "RESULTAT_SCORING", String.valueOf(dto.getIdDemandeCredit()), dto, "Execution scoring soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Obtenir le résultat du scoring", description = "Retourne le résultat du scoring calculé pour une demande de crédit, incluant le score total, la décision et le détail des points par critère.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Résultat du scoring retourné avec succès", content = @Content(schema = @Schema(implementation = ResultatScoring.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Résultat non trouvé pour cette demande", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/demandes/{idDemande}/resultat")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<ResultatScoring> getResultat(@PathVariable Long idDemande) {
        return scoringExtensionService.getResultatByDemande(idDemande)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private ActionEnAttenteResponseDTO toActionDto(ActionEnAttente action) {
        ActionEnAttenteResponseDTO dto = new ActionEnAttenteResponseDTO();
        dto.setIdActionEnAttente(action.getIdActionEnAttente());
        dto.setTypeAction(action.getTypeAction());
        dto.setRessource(action.getRessource());
        dto.setReferenceRessource(action.getReferenceRessource());
        dto.setStatut(action.getStatut());
        return dto;
    }
}
