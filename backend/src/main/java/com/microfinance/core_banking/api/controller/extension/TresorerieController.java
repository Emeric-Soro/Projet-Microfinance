package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.ApprovisionnerCaisseRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerCaisseRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerCoffreRequestDTO;
import com.microfinance.core_banking.dto.request.extension.DelesterCaisseRequestDTO;
import com.microfinance.core_banking.dto.request.extension.FermerSessionRequestDTO;
import com.microfinance.core_banking.dto.request.extension.OuvrirSessionRequestDTO;
import com.microfinance.core_banking.dto.response.extension.ActionEnAttenteResponseDTO;
import com.microfinance.core_banking.dto.response.extension.CaisseResponseDTO;
import com.microfinance.core_banking.dto.response.extension.CoffreResponseDTO;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.dto.response.extension.MouvementCoffreResponseDTO;
import com.microfinance.core_banking.dto.response.extension.SessionCaisseResponseDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.Caisse;
import com.microfinance.core_banking.entity.Coffre;
import com.microfinance.core_banking.entity.MouvementCoffre;
import com.microfinance.core_banking.entity.SessionCaisse;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import com.microfinance.core_banking.service.extension.TresorerieService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tresorerie")
@Tag(name = "Trésorerie", description = "API de gestion de la trésorerie (caisses, coffres, sessions, approvisionnements, délestages)")
public class TresorerieController {

    private final TresorerieService tresorerieService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public TresorerieController(TresorerieService tresorerieService, PendingActionSubmissionService pendingActionSubmissionService) {
        this.tresorerieService = tresorerieService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @Operation(summary = "Créer une caisse", description = "Crée une nouvelle caisse rattachée à une agence et éventuellement à un guichet. Transit par le workflow Maker-Checker pour validation. Définit le point de trésorerie physique où seront enregistrées les opérations de caisse.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Création caisse soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - code caisse déjà existant", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/caisses")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_MANAGE)")
    @AuditLog(action = "CAISSE_CREATE", resource = "CAISSE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerCaisse(@Valid @RequestBody CreerCaisseRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_CAISSE", "CAISSE", dto.getCodeCaisse(), dto, "Creation caisse soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Lister les caisses", description = "Retourne la liste de toutes les caisses avec leur code, libellé, agence de rattachement et solde théorique. Permet de consulter l'état des points de trésorerie disponibles dans l'institution.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des caisses retournée avec succès", content = @Content(schema = @Schema(implementation = CaisseResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/caisses")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_VIEW)")
    public ResponseEntity<List<CaisseResponseDTO>> listerCaisses() {
        return ResponseEntity.ok(tresorerieService.listerCaisses().stream().map(this::toDto).toList());
    }

    @Operation(summary = "Créer un coffre", description = "Crée un nouveau coffre rattaché à une agence pour le stockage sécurisé des fonds. Transit par le workflow Maker-Checker pour validation. Le coffre permet les mouvements d'espèces entre la caisse et le coffre-fort.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Création coffre soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - code coffre déjà existant", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/coffres")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_MANAGE)")
    @AuditLog(action = "COFFRE_CREATE", resource = "COFFRE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerCoffre(@Valid @RequestBody CreerCoffreRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_COFFRE", "COFFRE", dto.getCodeCoffre(), dto, "Creation coffre soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Lister les coffres", description = "Retourne la liste de tous les coffres avec leur code, libellé, agence et solde théorique. Permet de consulter l'état des coffres-forts disponibles dans l'institution.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des coffres retournée avec succès", content = @Content(schema = @Schema(implementation = CoffreResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/coffres")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_VIEW)")
    public ResponseEntity<List<CoffreResponseDTO>> listerCoffres() {
        return ResponseEntity.ok(tresorerieService.listerCoffres().stream().map(this::toDto).toList());
    }

    @Operation(summary = "Lister les mouvements d'un coffre", description = "Retourne l'historique des mouvements (entrées/sorties) d'un coffre identifié par son ID. Permet de tracer toutes les opérations affectant le solde du coffre : approvisionnements depuis la caisse et délestages vers la caisse.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des mouvements retournée avec succès", content = @Content(schema = @Schema(implementation = MouvementCoffreResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Coffre introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/coffres/{idCoffre}/mouvements")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_VIEW)")
    public ResponseEntity<List<MouvementCoffreResponseDTO>> listerMouvementsCoffre(@PathVariable Long idCoffre) {
        return ResponseEntity.ok(tresorerieService.listerMouvementsCoffre(idCoffre).stream().map(this::toDto).toList());
    }

    @Operation(summary = "Ouvrir une session de caisse", description = "Soumet une ouverture de session de caisse pour un utilisateur et une caisse donnés. Transit par le workflow Maker-Checker pour validation. La session enregistre le solde d'ouverture et permet de tracer les opérations de la journée. Précondition : la caisse ne doit pas avoir de session ouverte.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Ouverture de session soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - session déjà ouverte", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/sessions")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_MANAGE)")
    @AuditLog(action = "CAISSE_OPEN", resource = "SESSION_CAISSE")
    public ResponseEntity<ActionEnAttenteResponseDTO> ouvrirSession(@Valid @RequestBody OuvrirSessionRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("OPEN_SESSION_CAISSE", "SESSION_CAISSE", null, dto, "Ouverture de session soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Fermer une session de caisse", description = "Soumet la fermeture d'une session de caisse avec le solde physique constaté. Transit par le workflow Maker-Checker pour validation. Précondition : la session doit être ouverte. Effet comptable : calcul de l'écart entre solde théorique et solde physique, génération d'une écriture de régularisation si nécessaire.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Fermeture de session soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Session introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - session déjà fermée", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/sessions/{idSession}/fermeture")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_MANAGE)")
    @AuditLog(action = "CAISSE_CLOSE", resource = "SESSION_CAISSE")
    public ResponseEntity<ActionEnAttenteResponseDTO> fermerSession(@PathVariable Long idSession, @Valid @RequestBody FermerSessionRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CLOSE_SESSION_CAISSE", "SESSION_CAISSE", String.valueOf(idSession), dto, "Fermeture de session soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Lister les sessions de caisse", description = "Retourne la liste de toutes les sessions de caisse avec leur date d'ouverture, date de fermeture, soldes et statut. Permet de suivre l'historique des ouvertures et fermetures de caisses par utilisateur.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des sessions retournée avec succès", content = @Content(schema = @Schema(implementation = SessionCaisseResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/sessions")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_VIEW)")
    public ResponseEntity<List<SessionCaisseResponseDTO>> listerSessions() {
        return ResponseEntity.ok(tresorerieService.listerSessions().stream().map(this::toDto).toList());
    }

    @Operation(summary = "Approvisionner une caisse", description = "Soumet un approvisionnement de caisse depuis le coffre pour augmenter le fonds de caisse disponible. Transit par le workflow Maker-Checker pour validation. Effet comptable : transfert du coffre vers la caisse avec mise à jour des soldes respectifs. Génère un mouvement de coffre.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Approvisionnement soumis en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - solde coffre insuffisant", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/approvisionnements")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_MANAGE)")
    @AuditLog(action = "CAISSE_APPROVISION", resource = "APPROVISIONNEMENT_CAISSE")
    public ResponseEntity<ActionEnAttenteResponseDTO> approvisionnerCaisse(@Valid @RequestBody ApprovisionnerCaisseRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("APPROVISIONNEMENT_CAISSE", "APPROVISIONNEMENT_CAISSE", null, dto, "Approvisionnement caisse soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Délester une caisse", description = "Soumet un délestage de caisse vers le coffre pour réduire le montant d'espèces en caisse. Transit par le workflow Maker-Checker pour validation. Effet comptable : transfert de la caisse vers le coffre avec mise à jour des soldes. Génère un mouvement de coffre.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Délestage soumis en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - solde caisse insuffisant", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/delestages")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_MANAGE)")
    @AuditLog(action = "CAISSE_DELESTAGE", resource = "DELESTAGE_CAISSE")
    public ResponseEntity<ActionEnAttenteResponseDTO> delesterCaisse(@Valid @RequestBody DelesterCaisseRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("DELESTAGE_CAISSE", "DELESTAGE_CAISSE", null, dto, "Delestage caisse soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    private CaisseResponseDTO toDto(Caisse caisse) {
        CaisseResponseDTO dto = new CaisseResponseDTO();
        dto.setIdCaisse(caisse.getIdCaisse());
        dto.setCodeCaisse(caisse.getCodeCaisse());
        dto.setLibelle(caisse.getLibelle());
        dto.setAgence(caisse.getAgence().getNomAgence());
        dto.setGuichet(caisse.getGuichet() == null ? null : caisse.getGuichet().getNomGuichet());
        dto.setStatut(caisse.getStatut());
        dto.setSoldeTheorique(caisse.getSoldeTheorique());
        return dto;
    }

    private CoffreResponseDTO toDto(Coffre coffre) {
        CoffreResponseDTO dto = new CoffreResponseDTO();
        dto.setIdCoffre(coffre.getIdCoffre());
        dto.setCodeCoffre(coffre.getCodeCoffre());
        dto.setLibelle(coffre.getLibelle());
        dto.setAgence(coffre.getAgence().getNomAgence());
        dto.setSoldeTheorique(coffre.getSoldeTheorique());
        dto.setStatut(coffre.getStatut());
        return dto;
    }

    private SessionCaisseResponseDTO toDto(SessionCaisse session) {
        SessionCaisseResponseDTO dto = new SessionCaisseResponseDTO();
        dto.setIdSessionCaisse(session.getIdSessionCaisse());
        dto.setIdCaisse(session.getCaisse().getIdCaisse());
        dto.setIdUtilisateur(session.getUtilisateur().getIdUser());
        dto.setDateOuverture(session.getDateOuverture());
        dto.setDateFermeture(session.getDateFermeture());
        dto.setSoldeOuverture(session.getSoldeOuverture());
        dto.setSoldeTheoriqueFermeture(session.getSoldeTheoriqueFermeture());
        dto.setSoldePhysiqueFermeture(session.getSoldePhysiqueFermeture());
        dto.setEcart(session.getEcart());
        dto.setStatut(session.getStatut());
        return dto;
    }

    private MouvementCoffreResponseDTO toDto(MouvementCoffre mouvementCoffre) {
        MouvementCoffreResponseDTO dto = new MouvementCoffreResponseDTO();
        dto.setIdMouvementCoffre(mouvementCoffre.getIdMouvementCoffre());
        dto.setTypeMouvement(mouvementCoffre.getTypeMouvement());
        dto.setMontant(mouvementCoffre.getMontant());
        dto.setReferenceMouvement(mouvementCoffre.getReferenceMouvement());
        dto.setCommentaire(mouvementCoffre.getCommentaire());
        return dto;
    }

    private ActionEnAttenteResponseDTO toActionDto(ActionEnAttente action) {
        ActionEnAttenteResponseDTO dto = new ActionEnAttenteResponseDTO();
        dto.setIdActionEnAttente(action.getIdActionEnAttente());
        dto.setTypeAction(action.getTypeAction());
        dto.setRessource(action.getRessource());
        dto.setStatut(action.getStatut());
        return dto;
    }
}
