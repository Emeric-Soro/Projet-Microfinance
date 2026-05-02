package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.CreerBudgetRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerCommandeRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerFournisseurRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerImmobilisationRequestDTO;
import com.microfinance.core_banking.dto.request.extension.GenererBulletinPaieRequestDTO;
import com.microfinance.core_banking.dto.response.extension.ActionEnAttenteResponseDTO;
import com.microfinance.core_banking.dto.response.extension.BudgetExploitationResponseDTO;
import com.microfinance.core_banking.dto.response.extension.BulletinPaieResponseDTO;
import com.microfinance.core_banking.dto.response.extension.CommandeAchatResponseDTO;
import com.microfinance.core_banking.dto.response.extension.FournisseurResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ImmobilisationResponseDTO;
import com.microfinance.core_banking.dto.response.extension.LigneBudgetResponseDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.BudgetExploitation;
import com.microfinance.core_banking.entity.BulletinPaie;
import com.microfinance.core_banking.entity.CommandeAchat;
import com.microfinance.core_banking.entity.Fournisseur;
import com.microfinance.core_banking.entity.Immobilisation;
import com.microfinance.core_banking.entity.LigneBudget;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import com.microfinance.core_banking.service.extension.SupportEntrepriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
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
@RequestMapping("/api/support")
@Tag(name = "Support Entreprise", description = "API de gestion des ressources humaines, achats, immobilisations et budget")
public class SupportEntrepriseController {

    private final SupportEntrepriseService supportEntrepriseService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public SupportEntrepriseController(SupportEntrepriseService supportEntrepriseService, PendingActionSubmissionService pendingActionSubmissionService) {
        this.supportEntrepriseService = supportEntrepriseService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @PostMapping("/budgets")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SUPPORT_MANAGE)")
    @AuditLog(action = "SUPPORT_BUDGET_CREATE", resource = "BUDGET")
    @Operation(summary = "Créer un budget d'exploitation", description = "Soumet la création d'un nouveau budget d'exploitation via le workflow de validation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Demande de création de budget soumise et en attente de validation", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit - budget déjà existant", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> creerBudget(@Valid @RequestBody CreerBudgetRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_BUDGET", "BUDGET", dto.getCodeBudget(), dto, "Creation budget soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionEnAttenteDto(action));
    }

    @GetMapping("/budgets")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SUPPORT_VIEW)")
    @Operation(summary = "Lister les budgets d'exploitation", description = "Retourne la liste de tous les budgets d'exploitation enregistrés")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des budgets retournée avec succès", content = @Content(schema = @Schema(implementation = BudgetExploitationResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<BudgetExploitationResponseDTO>> listerBudgets() {
        return ResponseEntity.ok(supportEntrepriseService.listerBudgets().stream().map(this::toBudgetDto).toList());
    }

    @GetMapping("/budgets/{idBudget}/lignes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SUPPORT_VIEW)")
    @Operation(summary = "Lister les lignes d'un budget", description = "Retourne les lignes budgétaires détaillées pour un budget d'exploitation donné")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des lignes budgétaires retournée avec succès", content = @Content(schema = @Schema(implementation = LigneBudgetResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Budget non trouvé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<LigneBudgetResponseDTO>> listerLignesBudget(@PathVariable Long idBudget) {
        return ResponseEntity.ok(supportEntrepriseService.listerLignesBudget(idBudget).stream().map(this::toLigneBudgetDto).toList());
    }

    @PostMapping("/fournisseurs")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SUPPORT_MANAGE)")
    @AuditLog(action = "SUPPORT_SUPPLIER_CREATE", resource = "FOURNISSEUR")
    @Operation(summary = "Créer un fournisseur", description = "Soumet la création d'un nouveau fournisseur via le workflow de validation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Demande de création de fournisseur soumise et en attente de validation", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit - fournisseur déjà existant", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> creerFournisseur(@Valid @RequestBody CreerFournisseurRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_FOURNISSEUR", "FOURNISSEUR", dto.getCodeFournisseur(), dto, "Creation fournisseur soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionEnAttenteDto(action));
    }

    @GetMapping("/fournisseurs")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SUPPORT_VIEW)")
    @Operation(summary = "Lister les fournisseurs", description = "Retourne la liste de tous les fournisseurs enregistrés")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des fournisseurs retournée avec succès", content = @Content(schema = @Schema(implementation = FournisseurResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<FournisseurResponseDTO>> listerFournisseurs() {
        return ResponseEntity.ok(supportEntrepriseService.listerFournisseurs().stream().map(this::toFournisseurDto).toList());
    }

    @PostMapping("/commandes-achat")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SUPPORT_MANAGE)")
    @AuditLog(action = "SUPPORT_PURCHASE_ORDER_CREATE", resource = "COMMANDE_ACHAT")
    @Operation(summary = "Créer une commande d'achat", description = "Soumet la création d'une nouvelle commande d'achat via le workflow de validation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Demande de création de commande soumise et en attente de validation", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> creerCommande(@Valid @RequestBody CreerCommandeRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_COMMANDE_ACHAT", "COMMANDE_ACHAT", null, dto, "Creation commande achat soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionEnAttenteDto(action));
    }

    @GetMapping("/commandes-achat")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SUPPORT_VIEW)")
    @Operation(summary = "Lister les commandes d'achat", description = "Retourne la liste de toutes les commandes d'achat enregistrées")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des commandes retournée avec succès", content = @Content(schema = @Schema(implementation = CommandeAchatResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<CommandeAchatResponseDTO>> listerCommandes() {
        return ResponseEntity.ok(supportEntrepriseService.listerCommandesAchat().stream().map(this::toCommandeDto).toList());
    }

    @PostMapping("/bulletins-paie")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SUPPORT_MANAGE)")
    @AuditLog(action = "SUPPORT_PAYROLL_CREATE", resource = "BULLETIN_PAIE")
    @Operation(summary = "Générer un bulletin de paie", description = "Soumet une demande de génération de bulletin de paie via le workflow de validation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Demande de génération de bulletin soumise et en attente de validation", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> genererBulletinPaie(@Valid @RequestBody GenererBulletinPaieRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_BULLETIN_PAIE", "BULLETIN_PAIE", dto.getPeriodePaie(), dto, "Generation bulletin paie soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionEnAttenteDto(action));
    }

    @GetMapping("/bulletins-paie")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SUPPORT_VIEW)")
    @Operation(summary = "Lister les bulletins de paie", description = "Retourne la liste de tous les bulletins de paie générés")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des bulletins retournée avec succès", content = @Content(schema = @Schema(implementation = BulletinPaieResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<BulletinPaieResponseDTO>> listerBulletinsPaie() {
        return ResponseEntity.ok(supportEntrepriseService.listerBulletinsPaie().stream().map(this::toBulletinDto).toList());
    }

    @PostMapping("/immobilisations")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SUPPORT_MANAGE)")
    @AuditLog(action = "SUPPORT_ASSET_CREATE", resource = "IMMOBILISATION")
    @Operation(summary = "Créer une immobilisation", description = "Soumet la création d'une nouvelle immobilisation via le workflow de validation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Demande de création d'immobilisation soumise et en attente de validation", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit - immobilisation déjà existante", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> creerImmobilisation(@Valid @RequestBody CreerImmobilisationRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_IMMOBILISATION", "IMMOBILISATION", dto.getCodeImmobilisation(), dto, "Creation immobilisation soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionEnAttenteDto(action));
    }

    @GetMapping("/immobilisations")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SUPPORT_VIEW)")
    @Operation(summary = "Lister les immobilisations", description = "Retourne la liste de toutes les immobilisations enregistrées")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des immobilisations retournée avec succès", content = @Content(schema = @Schema(implementation = ImmobilisationResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<ImmobilisationResponseDTO>> listerImmobilisations() {
        return ResponseEntity.ok(supportEntrepriseService.listerImmobilisations().stream().map(this::toImmobilisationDto).toList());
    }

    private ActionEnAttenteResponseDTO toActionEnAttenteDto(ActionEnAttente action) {
        ActionEnAttenteResponseDTO dto = new ActionEnAttenteResponseDTO();
        dto.setIdActionEnAttente(action.getIdActionEnAttente());
        dto.setTypeAction(action.getTypeAction());
        dto.setRessource(action.getRessource());
        dto.setReferenceRessource(action.getReferenceRessource());
        dto.setStatut(action.getStatut());
        return dto;
    }

    private BudgetExploitationResponseDTO toBudgetDto(BudgetExploitation budget) {
        BudgetExploitationResponseDTO dto = new BudgetExploitationResponseDTO();
        dto.setIdBudget(budget.getIdBudget());
        dto.setCodeBudget(budget.getCodeBudget());
        dto.setAnnee(budget.getAnnee());
        dto.setAgence(budget.getAgence() == null ? null : budget.getAgence().getNomAgence());
        dto.setMontantTotal(budget.getMontantTotal());
        dto.setStatut(budget.getStatut());
        return dto;
    }

    private LigneBudgetResponseDTO toLigneBudgetDto(LigneBudget ligneBudget) {
        LigneBudgetResponseDTO dto = new LigneBudgetResponseDTO();
        dto.setIdLigneBudget(ligneBudget.getIdLigneBudget());
        dto.setRubrique(ligneBudget.getRubrique());
        dto.setMontantPrevu(ligneBudget.getMontantPrevu());
        dto.setMontantEngage(ligneBudget.getMontantEngage());
        dto.setMontantConsomme(ligneBudget.getMontantConsomme());
        return dto;
    }

    private FournisseurResponseDTO toFournisseurDto(Fournisseur fournisseur) {
        FournisseurResponseDTO dto = new FournisseurResponseDTO();
        dto.setIdFournisseur(fournisseur.getIdFournisseur());
        dto.setCodeFournisseur(fournisseur.getCodeFournisseur());
        dto.setNom(fournisseur.getNom());
        dto.setContact(fournisseur.getContact());
        dto.setTelephone(fournisseur.getTelephone());
        dto.setEmail(fournisseur.getEmail());
        dto.setStatut(fournisseur.getStatut());
        return dto;
    }

    private CommandeAchatResponseDTO toCommandeDto(CommandeAchat commandeAchat) {
        CommandeAchatResponseDTO dto = new CommandeAchatResponseDTO();
        dto.setIdCommandeAchat(commandeAchat.getIdCommandeAchat());
        dto.setReferenceCommande(commandeAchat.getReferenceCommande());
        dto.setFournisseur(commandeAchat.getFournisseur().getNom());
        dto.setAgence(commandeAchat.getAgence() == null ? null : commandeAchat.getAgence().getNomAgence());
        dto.setObjet(commandeAchat.getObjet());
        dto.setMontant(commandeAchat.getMontant());
        dto.setDateCommande(commandeAchat.getDateCommande());
        dto.setStatut(commandeAchat.getStatut());
        return dto;
    }

    private BulletinPaieResponseDTO toBulletinDto(BulletinPaie bulletinPaie) {
        BulletinPaieResponseDTO dto = new BulletinPaieResponseDTO();
        dto.setIdBulletinPaie(bulletinPaie.getIdBulletinPaie());
        dto.setEmploye(bulletinPaie.getEmploye().getNomComplet());
        dto.setPeriode(bulletinPaie.getPeriode());
        dto.setSalaireBrut(bulletinPaie.getSalaireBrut());
        dto.setRetenues(bulletinPaie.getRetenues());
        dto.setSalaireNet(bulletinPaie.getSalaireNet());
        dto.setStatut(bulletinPaie.getStatut());
        return dto;
    }

    private ImmobilisationResponseDTO toImmobilisationDto(Immobilisation immobilisation) {
        ImmobilisationResponseDTO dto = new ImmobilisationResponseDTO();
        dto.setIdImmobilisation(immobilisation.getIdImmobilisation());
        dto.setCodeImmobilisation(immobilisation.getCodeImmobilisation());
        dto.setLibelle(immobilisation.getLibelle());
        dto.setAgence(immobilisation.getAgence() == null ? null : immobilisation.getAgence().getNomAgence());
        dto.setValeurOrigine(immobilisation.getValeurOrigine());
        dto.setDureeAmortissementMois(immobilisation.getDureeAmortissementMois());
        dto.setAmortissementMensuel(immobilisation.getAmortissementMensuel());
        dto.setValeurNette(immobilisation.getValeurNette());
        dto.setDateAcquisition(immobilisation.getDateAcquisition());
        dto.setStatut(immobilisation.getStatut());
        return dto;
    }
}
