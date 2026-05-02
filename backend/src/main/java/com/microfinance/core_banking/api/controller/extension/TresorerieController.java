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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
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
public class TresorerieController {

    private final TresorerieService tresorerieService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public TresorerieController(TresorerieService tresorerieService, PendingActionSubmissionService pendingActionSubmissionService) {
        this.tresorerieService = tresorerieService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @Operation(summary = "Creer une caisse", description = "Cree une nouvelle caisse (soumis pour approbation)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Creation caisse soumise en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/caisses")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_MANAGE)")
    @AuditLog(action = "CAISSE_CREATE", resource = "CAISSE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerCaisse(@Valid @RequestBody CreerCaisseRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_CAISSE", "CAISSE", dto.getCodeCaisse(), dto, "Creation caisse soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Lister les caisses", description = "Retourne la liste de toutes les caisses")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des caisses retournee avec succes"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/caisses")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_VIEW)")
    public ResponseEntity<List<CaisseResponseDTO>> listerCaisses() {
        return ResponseEntity.ok(tresorerieService.listerCaisses().stream().map(this::toDto).toList());
    }

    @Operation(summary = "Creer un coffre", description = "Cree un nouveau coffre (soumis pour approbation)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Creation coffre soumise en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/coffres")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_MANAGE)")
    @AuditLog(action = "COFFRE_CREATE", resource = "COFFRE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerCoffre(@Valid @RequestBody CreerCoffreRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_COFFRE", "COFFRE", dto.getCodeCoffre(), dto, "Creation coffre soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Lister les coffres", description = "Retourne la liste de tous les coffres")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des coffres retournee avec succes"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/coffres")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_VIEW)")
    public ResponseEntity<List<CoffreResponseDTO>> listerCoffres() {
        return ResponseEntity.ok(tresorerieService.listerCoffres().stream().map(this::toDto).toList());
    }

    @Operation(summary = "Lister les mouvements d'un coffre", description = "Retourne l'historique des mouvements d'un coffre")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des mouvements retournee avec succes"),
        @ApiResponse(responseCode = "403", description = "Acces refuse"),
        @ApiResponse(responseCode = "404", description = "Coffre introuvable")
    })
    @GetMapping("/coffres/{idCoffre}/mouvements")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_VIEW)")
    public ResponseEntity<List<MouvementCoffreResponseDTO>> listerMouvementsCoffre(@PathVariable Long idCoffre) {
        return ResponseEntity.ok(tresorerieService.listerMouvementsCoffre(idCoffre).stream().map(this::toDto).toList());
    }

    @Operation(summary = "Ouvrir une session de caisse", description = "Soumet une ouverture de session de caisse pour approbation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Ouverture de session soumise en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/sessions")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_MANAGE)")
    @AuditLog(action = "CAISSE_OPEN", resource = "SESSION_CAISSE")
    public ResponseEntity<ActionEnAttenteResponseDTO> ouvrirSession(@Valid @RequestBody OuvrirSessionRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("OPEN_SESSION_CAISSE", "SESSION_CAISSE", null, dto, "Ouverture de session soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Fermer une session de caisse", description = "Soumet une fermeture de session de caisse pour approbation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Fermeture de session soumise en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse"),
        @ApiResponse(responseCode = "404", description = "Session introuvable")
    })
    @PutMapping("/sessions/{idSession}/fermeture")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_MANAGE)")
    @AuditLog(action = "CAISSE_CLOSE", resource = "SESSION_CAISSE")
    public ResponseEntity<ActionEnAttenteResponseDTO> fermerSession(@PathVariable Long idSession, @Valid @RequestBody FermerSessionRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CLOSE_SESSION_CAISSE", "SESSION_CAISSE", String.valueOf(idSession), dto, "Fermeture de session soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Lister les sessions de caisse", description = "Retourne la liste de toutes les sessions de caisse")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des sessions retournee avec succes"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/sessions")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_VIEW)")
    public ResponseEntity<List<SessionCaisseResponseDTO>> listerSessions() {
        return ResponseEntity.ok(tresorerieService.listerSessions().stream().map(this::toDto).toList());
    }

    @Operation(summary = "Approvisionner une caisse", description = "Soumet un approvisionnement de caisse pour approbation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Approvisionnement soumis en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/approvisionnements")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_TREASURY_MANAGE)")
    @AuditLog(action = "CAISSE_APPROVISION", resource = "APPROVISIONNEMENT_CAISSE")
    public ResponseEntity<ActionEnAttenteResponseDTO> approvisionnerCaisse(@Valid @RequestBody ApprovisionnerCaisseRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("APPROVISIONNEMENT_CAISSE", "APPROVISIONNEMENT_CAISSE", null, dto, "Approvisionnement caisse soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Delester une caisse", description = "Soumet un delestage de caisse pour approbation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Delestage soumis en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
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
