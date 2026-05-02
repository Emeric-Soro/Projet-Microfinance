package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.dto.request.extension.ClotureComptableRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerClasseComptableRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerCompteComptableRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerEcritureManuelleRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerJournalComptableRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerSchemaComptableRequestDTO;
import com.microfinance.core_banking.dto.request.extension.TesterSchemaComptableRequestDTO;
import com.microfinance.core_banking.dto.response.extension.ActionEnAttenteResponseDTO;
import com.microfinance.core_banking.dto.response.extension.BalanceLineDTO;
import com.microfinance.core_banking.dto.response.extension.BootstrapResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ClasseComptableResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ClotureComptableResponseDTO;
import com.microfinance.core_banking.dto.response.extension.CompteComptableResponseDTO;
import com.microfinance.core_banking.dto.response.extension.EcritureComptableResponseDTO;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.dto.response.extension.JournalComptableResponseDTO;
import com.microfinance.core_banking.dto.response.extension.LigneGrandLivreDTO;
import com.microfinance.core_banking.dto.response.extension.ControlesComptablesResponseDTO;
import com.microfinance.core_banking.dto.response.extension.SchemaComptableResponseDTO;
import com.microfinance.core_banking.dto.response.extension.SchemaTestResponseDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.ClasseComptable;
import com.microfinance.core_banking.entity.ClotureComptable;
import com.microfinance.core_banking.entity.CompteComptable;
import com.microfinance.core_banking.entity.EcritureComptable;
import com.microfinance.core_banking.entity.JournalComptable;
import com.microfinance.core_banking.entity.SchemaComptable;
import com.microfinance.core_banking.service.extension.ComptabiliteExtensionService;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/comptabilite")
@Tag(name = "Comptabilité", description = "API de gestion de la comptabilité bancaire (classes, comptes, journaux, écritures, balance, clôtures)")
public class ComptabiliteExtensionController {

    private final ComptabiliteExtensionService comptabiliteExtensionService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public ComptabiliteExtensionController(
            ComptabiliteExtensionService comptabiliteExtensionService,
            PendingActionSubmissionService pendingActionSubmissionService
    ) {
        this.comptabiliteExtensionService = comptabiliteExtensionService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @Operation(summary = "Initialiser le référentiel comptable", description = "Crée les classes et comptes comptables par défaut nécessaires au fonctionnement du système bancaire. Génère le plan comptable de l'institution selon les normes en vigueur. Effet comptable : création de l'ensemble des comptes de base (actif, passif, charges, produits). Nécessite le rôle administrateur.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Référentiel initialisé avec succès", content = @Content(schema = @Schema(implementation = BootstrapResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - référentiel déjà initialisé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/bootstrap")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_BOOTSTRAP", resource = "COMPTABILITE")
    public ResponseEntity<BootstrapResponseDTO> bootstrap() {
        return ResponseEntity.ok(comptabiliteExtensionService.bootstrapReferentiel());
    }

    @Operation(summary = "Créer une classe comptable", description = "Crée une nouvelle classe comptable dans le plan comptable (ex: classe 1 - Capitaux propres). Transit par le workflow Maker-Checker pour validation. La classe définit la structure de premier niveau du référentiel comptable.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Création classe comptable soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - code classe déjà existant", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/classes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_CLASS_CREATE", resource = "CLASSE_COMPTABLE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerClasse(@Valid @RequestBody CreerClasseComptableRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_CLASSE_COMPTABLE", "CLASSE_COMPTABLE", dto.getCodeClasse(), dto, "Creation classe comptable soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Créer un compte comptable", description = "Crée un nouveau compte comptable rattaché à une classe existante (ex: compte 101 - Capital social). Transit par le workflow Maker-Checker pour validation. Définit le type de solde (débiteur/créditeur) et l'affectation à une agence. Effet comptable : le compte est utilisable dans les écritures après activation.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Création compte comptable soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Classe comptable introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - numéro de compte déjà existant", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/comptes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_ACCOUNT_CREATE", resource = "COMPTE_COMPTABLE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerCompte(@Valid @RequestBody CreerCompteComptableRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_COMPTE_COMPTABLE", "COMPTE_COMPTABLE", dto.getNumeroCompte(), dto, "Creation compte comptable soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Créer un journal comptable", description = "Crée un nouveau journal comptable (ex: journal des opérations diverses, journal de banque). Transit par le workflow Maker-Checker pour validation. Le journal sert de regroupement pour les écritures comptables de même nature.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Création journal comptable soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - code journal déjà existant", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/journaux")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_JOURNAL_CREATE", resource = "JOURNAL_COMPTABLE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerJournal(@Valid @RequestBody CreerJournalComptableRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_JOURNAL_COMPTABLE", "JOURNAL_COMPTABLE", dto.getCodeJournal(), dto, "Creation journal comptable soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Créer un schéma comptable", description = "Crée un nouveau schéma comptable définissant les règles d'écriture automatique pour une opération métier (débit/crédit). Transit par le workflow Maker-Checker pour validation. Le schéma est utilisé par les modules métier pour générer automatiquement les écritures comptables.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Création schéma comptable soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - code opération déjà existant", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/schemas")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_SCHEMA_CREATE", resource = "SCHEMA_COMPTABLE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerSchema(@Valid @RequestBody CreerSchemaComptableRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_SCHEMA_COMPTABLE", "SCHEMA_COMPTABLE", dto.getCodeOperation(), dto, "Creation schema comptable soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Créer une écriture manuelle", description = "Crée une écriture comptable manuelle (pièce comptable). Transit par le workflow Maker-Checker pour validation. Permet de saisir des écritures qui ne sont pas générées automatiquement par les modules métier. Effet comptable : impacte immédiatement les soldes des comptes concernés après validation.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Écriture manuelle soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - écriture non équilibrée", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/ecritures")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_MANUAL_ENTRY_CREATE", resource = "ECRITURE_COMPTABLE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerEcriture(@Valid @RequestBody CreerEcritureManuelleRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_ECRITURE_MANUELLE", "ECRITURE_COMPTABLE", dto.getReferencePiece(), dto, "Ecriture manuelle soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Soumettre une clôture comptable", description = "Soumet une clôture comptable générale pour une période donnée. Transit par le workflow Maker-Checker pour approbation. Précondition : toutes les écritures de la période doivent être validées. Effet comptable : arrête les comptes de la période et calcule le résultat. Les écritures ultérieures ne peuvent pas impacter la période clôturée.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Clôture comptable soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - période déjà clôturée", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/clotures")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_CLOSE", resource = "CLOTURE_COMPTABLE")
    public ResponseEntity<ActionEnAttenteResponseDTO> cloturer(@Valid @RequestBody ClotureComptableRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CLOTURE_COMPTABLE", "CLOTURE_COMPTABLE", dto.getDateFin() != null ? dto.getDateFin().toString() : null, dto, "Cloture comptable soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Soumettre une clôture mensuelle", description = "Soumet une clôture comptable mensuelle pour arrêter les comptes d'un mois donné. Transit par le workflow Maker-Checker pour approbation. Effet comptable : calcul du résultat mensuel et vérification de la balance avant report des soldes sur le mois suivant.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Clôture mensuelle soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - mois déjà clôturé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/clotures/mensuelles")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_MONTHLY_CLOSE_SUBMIT", resource = "CLOTURE_COMPTABLE")
    public ResponseEntity<ActionEnAttenteResponseDTO> clotureMensuelle(@Valid @RequestBody ClotureComptableRequestDTO dto) {
        dto.setTypeCloture("MENSUELLE");
        ActionEnAttente action = pendingActionSubmissionService.submit("CLOTURE_MENSUELLE", "CLOTURE_COMPTABLE", dto.getDateFin() != null ? dto.getDateFin().toString() : null, dto, "Cloture mensuelle soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Soumettre une clôture annuelle", description = "Soumet une clôture comptable annuelle pour arrêter les comptes de fin d'exercice. Transit par le workflow Maker-Checker pour approbation. Effet comptable : clôture définitive de l'exercice, calcul du résultat annuel, affectation du résultat et réouverture des comptes pour le nouvel exercice.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Clôture annuelle soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - exercice déjà clôturé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/clotures/annuelles")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_YEARLY_CLOSE_SUBMIT", resource = "CLOTURE_COMPTABLE")
    public ResponseEntity<ActionEnAttenteResponseDTO> clotureAnnuelle(@Valid @RequestBody ClotureComptableRequestDTO dto) {
        dto.setTypeCloture("ANNUELLE");
        ActionEnAttente action = pendingActionSubmissionService.submit("CLOTURE_ANNUELLE", "CLOTURE_COMPTABLE", dto.getDateFin() != null ? dto.getDateFin().toString() : null, dto, "Cloture annuelle soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Lister les classes comptables", description = "Retourne la liste de toutes les classes comptables du plan comptable. Permet de consulter la structure hiérarchique des comptes (classe 1 à 9) avec leur code et libellé.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des classes retournée avec succès", content = @Content(schema = @Schema(implementation = ClasseComptableResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/classes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<List<ClasseComptableResponseDTO>> listerClasses() {
        return ResponseEntity.ok(comptabiliteExtensionService.listerClasses().stream().map(this::toClasseDto).toList());
    }

    @Operation(summary = "Lister les comptes comptables", description = "Retourne la liste de tous les comptes comptables avec leur numéro, intitulé et type de solde. Permet de consulter l'intégralité du plan comptable utilisé par l'institution.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des comptes retournée avec succès", content = @Content(schema = @Schema(implementation = CompteComptableResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/comptes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<List<CompteComptableResponseDTO>> listerComptes() {
        return ResponseEntity.ok(comptabiliteExtensionService.listerComptes().stream().map(this::toCompteDto).toList());
    }

    @Operation(summary = "Lister les journaux comptables", description = "Retourne la liste de tous les journaux comptables avec leur code, libellé et type. Permet de consulter les journaux disponibles pour l'enregistrement des opérations comptables.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des journaux retournée avec succès", content = @Content(schema = @Schema(implementation = JournalComptableResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/journaux")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<List<JournalComptableResponseDTO>> listerJournaux() {
        return ResponseEntity.ok(comptabiliteExtensionService.listerJournaux().stream().map(this::toJournalDto).toList());
    }

    @Operation(summary = "Lister les schémas comptables", description = "Retourne la liste de tous les schémas comptables configurés. Permet de consulter les règles d'écriture automatique (compte de débit, compte de crédit) pour chaque type d'opération métier.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des schémas retournée avec succès", content = @Content(schema = @Schema(implementation = SchemaComptableResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/schemas")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<List<SchemaComptableResponseDTO>> listerSchemas() {
        return ResponseEntity.ok(comptabiliteExtensionService.listerSchemas().stream().map(this::toSchemaDto).toList());
    }

    @Operation(summary = "Tester un schéma comptable", description = "Simule l'exécution d'un schéma comptable et retourne les écritures qui seraient générées sans les valider. Permet de vérifier le bon paramétrage des règles de comptabilisation avant de les activer. Aucun impact comptable.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Test exécuté avec succès", content = @Content(schema = @Schema(implementation = SchemaTestResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Schéma comptable introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/schemas/test")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_SCHEMA_TEST", resource = "SCHEMA_COMPTABLE")
    public ResponseEntity<SchemaTestResponseDTO> testerSchema(@Valid @RequestBody TesterSchemaComptableRequestDTO dto) {
        return ResponseEntity.ok(comptabiliteExtensionService.testerSchemaComptable(dto));
    }

    @Operation(summary = "Lister les écritures comptables", description = "Retourne les écritures comptables avec possibilité de filtrage par période et par journal. Permet de consulter l'historique des pièces comptables enregistrées dans le système avec leur référence, libellé et statut.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des écritures retournée avec succès", content = @Content(schema = @Schema(implementation = EcritureComptableResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Paramètres invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/ecritures")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<List<EcritureComptableResponseDTO>> listerEcritures(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) String codeJournal
    ) {
        return ResponseEntity.ok(comptabiliteExtensionService.listerEcritures(dateDebut, dateFin, codeJournal).stream().map(this::toEcritureDto).toList());
    }

    @Operation(summary = "Lister les pièces comptables", description = "Retourne les pièces comptables (écritures) avec filtrage par période et journal. Synonyme fonctionnel de la liste des écritures, présenté sous l'angle des pièces justificatives pour faciliter le travail des comptables.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des pièces retournée avec succès", content = @Content(schema = @Schema(implementation = EcritureComptableResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Paramètres invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/pieces")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<List<EcritureComptableResponseDTO>> listerPieces(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) String codeJournal
    ) {
        return ResponseEntity.ok(comptabiliteExtensionService.listerEcritures(dateDebut, dateFin, codeJournal).stream().map(this::toEcritureDto).toList());
    }

    @Operation(summary = "Consulter le grand livre", description = "Retourne les lignes du grand livre pour un compte comptable donné sur une période. Permet de visualiser le détail de toutes les mouvements (débit, crédit, solde) d'un compte spécifique. Essentiel pour le reporting comptable et l'audit.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Grand livre retourné avec succès", content = @Content(schema = @Schema(implementation = LigneGrandLivreDTO.class))),
        @ApiResponse(responseCode = "400", description = "Paramètres invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Compte comptable introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/grand-livre")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<List<LigneGrandLivreDTO>> grandLivre(
            @RequestParam String numeroCompte,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin
    ) {
        return ResponseEntity.ok(comptabiliteExtensionService.consulterGrandLivre(numeroCompte, dateDebut, dateFin));
    }

    @Operation(summary = "Consulter la balance", description = "Retourne la balance des comptes pour une période donnée avec les soldes débiteurs et créditeurs. Permet de vérifier l'équilibre comptable avant clôture. Chaque ligne présente un compte avec son total débit, total crédit et solde.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Balance retournée avec succès", content = @Content(schema = @Schema(implementation = BalanceLineDTO.class))),
        @ApiResponse(responseCode = "400", description = "Paramètres invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/balance")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<List<BalanceLineDTO>> balance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin
    ) {
        return ResponseEntity.ok(comptabiliteExtensionService.consulterBalance(dateDebut, dateFin));
    }

    @Operation(summary = "Effectuer les contrôles comptables", description = "Exécute les contrôles de cohérence comptable pour une période donnée. Vérifie l'équilibre des écritures, la continuité des séquences, la validité des comptes utilisés et l'absence d'anomalies. Retourne un rapport de synthèse des contrôles effectués.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contrôles exécutés avec succès", content = @Content(schema = @Schema(implementation = ControlesComptablesResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Paramètres invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/controles")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<ControlesComptablesResponseDTO> controles(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin
    ) {
        return ResponseEntity.ok(comptabiliteExtensionService.controlesComptables(dateDebut, dateFin));
    }

    @Operation(summary = "Lister les clôtures comptables", description = "Retourne la liste des clôtures comptables effectuées avec leur type (mensuelle, annuelle), période et statut. Permet de consulter l'historique des clôtures et de vérifier les périodes verrouillées.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des clôtures retournée avec succès", content = @Content(schema = @Schema(implementation = ClotureComptableResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Paramètres invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/clotures")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<List<ClotureComptableResponseDTO>> listerClotures(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin
    ) {
        return ResponseEntity.ok(comptabiliteExtensionService.listerClotures(dateDebut, dateFin).stream().map(this::toClotureDto).toList());
    }

    private ClasseComptableResponseDTO toClasseDto(ClasseComptable classe) {
        ClasseComptableResponseDTO dto = new ClasseComptableResponseDTO();
        dto.setIdClasseComptable(classe.getIdClasseComptable());
        dto.setCodeClasse(classe.getCodeClasse());
        dto.setLibelle(classe.getLibelle());
        dto.setOrdreAffichage(classe.getOrdreAffichage());
        return dto;
    }

    private CompteComptableResponseDTO toCompteDto(CompteComptable compte) {
        CompteComptableResponseDTO dto = new CompteComptableResponseDTO();
        dto.setIdCompteComptable(compte.getIdCompteComptable());
        dto.setNumeroCompte(compte.getNumeroCompte());
        dto.setIntitule(compte.getIntitule());
        dto.setTypeSolde(compte.getTypeSolde());
        dto.setCompteInterne(compte.getCompteInterne());
        dto.setClasse(compte.getClasseComptable() == null ? null : compte.getClasseComptable().getCodeClasse());
        dto.setAgence(compte.getAgence() == null ? null : compte.getAgence().getNomAgence());
        return dto;
    }

    private JournalComptableResponseDTO toJournalDto(JournalComptable journal) {
        JournalComptableResponseDTO dto = new JournalComptableResponseDTO();
        dto.setIdJournalComptable(journal.getIdJournalComptable());
        dto.setCodeJournal(journal.getCodeJournal());
        dto.setLibelle(journal.getLibelle());
        dto.setTypeJournal(journal.getTypeJournal());
        dto.setActif(journal.getActif());
        return dto;
    }

    private SchemaComptableResponseDTO toSchemaDto(SchemaComptable schema) {
        SchemaComptableResponseDTO dto = new SchemaComptableResponseDTO();
        dto.setIdSchemaComptable(schema.getIdSchemaComptable());
        dto.setCodeOperation(schema.getCodeOperation());
        dto.setCompteDebit(schema.getCompteDebit());
        dto.setCompteCredit(schema.getCompteCredit());
        dto.setCompteFrais(schema.getCompteFrais());
        dto.setJournalCode(schema.getJournalCode());
        dto.setActif(schema.getActif());
        return dto;
    }

    private EcritureComptableResponseDTO toEcritureDto(EcritureComptable ecriture) {
        EcritureComptableResponseDTO dto = new EcritureComptableResponseDTO();
        dto.setIdEcritureComptable(ecriture.getIdEcritureComptable());
        dto.setReferencePiece(ecriture.getReferencePiece());
        dto.setJournal(ecriture.getJournalComptable().getCodeJournal());
        dto.setDateComptable(ecriture.getDateComptable());
        dto.setDateValeur(ecriture.getDateValeur());
        dto.setLibelle(ecriture.getLibelle());
        dto.setSourceType(ecriture.getSourceType());
        dto.setSourceReference(ecriture.getSourceReference());
        dto.setStatut(ecriture.getStatut());
        return dto;
    }

    private ClotureComptableResponseDTO toClotureDto(ClotureComptable cloture) {
        ClotureComptableResponseDTO dto = new ClotureComptableResponseDTO();
        dto.setIdClotureComptable(cloture.getIdClotureComptable());
        dto.setTypeCloture(cloture.getTypeCloture());
        dto.setDateDebut(cloture.getDateDebut());
        dto.setDateFin(cloture.getDateFin());
        dto.setTotalEcritures(cloture.getTotalEcritures());
        dto.setStatut(cloture.getStatut());
        dto.setCommentaire(cloture.getCommentaire());
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
