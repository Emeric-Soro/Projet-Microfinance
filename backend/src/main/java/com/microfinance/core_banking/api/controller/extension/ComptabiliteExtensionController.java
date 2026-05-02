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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
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

    @Operation(summary = "Initialiser le referentiel comptable", description = "Cree les classes et comptes comptables par defaut")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Referentiel initialise avec succes"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/bootstrap")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_BOOTSTRAP", resource = "COMPTABILITE")
    public ResponseEntity<BootstrapResponseDTO> bootstrap() {
        return ResponseEntity.ok(comptabiliteExtensionService.bootstrapReferentiel());
    }

    @Operation(summary = "Creer une classe comptable", description = "Cree une nouvelle classe comptable (soumis pour approbation)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Creation classe comptable soumise en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/classes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_CLASS_CREATE", resource = "CLASSE_COMPTABLE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerClasse(@Valid @RequestBody CreerClasseComptableRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_CLASSE_COMPTABLE", "CLASSE_COMPTABLE", dto.getCodeClasse(), dto, "Creation classe comptable soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Creer un compte comptable", description = "Cree un nouveau compte comptable (soumis pour approbation)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Creation compte comptable soumise en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/comptes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_ACCOUNT_CREATE", resource = "COMPTE_COMPTABLE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerCompte(@Valid @RequestBody CreerCompteComptableRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_COMPTE_COMPTABLE", "COMPTE_COMPTABLE", dto.getNumeroCompte(), dto, "Creation compte comptable soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Creer un journal comptable", description = "Cree un nouveau journal comptable (soumis pour approbation)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Creation journal comptable soumise en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/journaux")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_JOURNAL_CREATE", resource = "JOURNAL_COMPTABLE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerJournal(@Valid @RequestBody CreerJournalComptableRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_JOURNAL_COMPTABLE", "JOURNAL_COMPTABLE", dto.getCodeJournal(), dto, "Creation journal comptable soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Creer un schema comptable", description = "Cree un nouveau schema comptable (soumis pour approbation)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Creation schema comptable soumise en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/schemas")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_SCHEMA_CREATE", resource = "SCHEMA_COMPTABLE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerSchema(@Valid @RequestBody CreerSchemaComptableRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_SCHEMA_COMPTABLE", "SCHEMA_COMPTABLE", dto.getCodeOperation(), dto, "Creation schema comptable soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Creer une ecriture manuelle", description = "Cree une ecriture comptable manuelle (soumis pour approbation)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Ecriture manuelle soumise en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/ecritures")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_MANUAL_ENTRY_CREATE", resource = "ECRITURE_COMPTABLE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerEcriture(@Valid @RequestBody CreerEcritureManuelleRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_ECRITURE_MANUELLE", "ECRITURE_COMPTABLE", dto.getReferencePiece(), dto, "Ecriture manuelle soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Soumettre une cloture comptable", description = "Soumet une cloture comptable pour approbation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Cloture comptable soumise en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/clotures")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_CLOSE", resource = "CLOTURE_COMPTABLE")
    public ResponseEntity<ActionEnAttenteResponseDTO> cloturer(@Valid @RequestBody ClotureComptableRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CLOTURE_COMPTABLE", "CLOTURE_COMPTABLE", dto.getDateFin() != null ? dto.getDateFin().toString() : null, dto, "Cloture comptable soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Soumettre une cloture mensuelle", description = "Soumet une cloture comptable mensuelle pour approbation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Cloture mensuelle soumise en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/clotures/mensuelles")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_MONTHLY_CLOSE_SUBMIT", resource = "CLOTURE_COMPTABLE")
    public ResponseEntity<ActionEnAttenteResponseDTO> clotureMensuelle(@Valid @RequestBody ClotureComptableRequestDTO dto) {
        dto.setTypeCloture("MENSUELLE");
        ActionEnAttente action = pendingActionSubmissionService.submit("CLOTURE_MENSUELLE", "CLOTURE_COMPTABLE", dto.getDateFin() != null ? dto.getDateFin().toString() : null, dto, "Cloture mensuelle soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Soumettre une cloture annuelle", description = "Soumet une cloture comptable annuelle pour approbation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Cloture annuelle soumise en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/clotures/annuelles")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_MANAGE)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_YEARLY_CLOSE_SUBMIT", resource = "CLOTURE_COMPTABLE")
    public ResponseEntity<ActionEnAttenteResponseDTO> clotureAnnuelle(@Valid @RequestBody ClotureComptableRequestDTO dto) {
        dto.setTypeCloture("ANNUELLE");
        ActionEnAttente action = pendingActionSubmissionService.submit("CLOTURE_ANNUELLE", "CLOTURE_COMPTABLE", dto.getDateFin() != null ? dto.getDateFin().toString() : null, dto, "Cloture annuelle soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Lister les classes comptables", description = "Retourne la liste de toutes les classes comptables")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des classes retournee avec succes"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/classes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<List<ClasseComptableResponseDTO>> listerClasses() {
        return ResponseEntity.ok(comptabiliteExtensionService.listerClasses().stream().map(this::toClasseDto).toList());
    }

    @Operation(summary = "Lister les comptes comptables", description = "Retourne la liste de tous les comptes comptables")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des comptes retournee avec succes"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/comptes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<List<CompteComptableResponseDTO>> listerComptes() {
        return ResponseEntity.ok(comptabiliteExtensionService.listerComptes().stream().map(this::toCompteDto).toList());
    }

    @Operation(summary = "Lister les journaux comptables", description = "Retourne la liste de tous les journaux comptables")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des journaux retournee avec succes"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/journaux")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<List<JournalComptableResponseDTO>> listerJournaux() {
        return ResponseEntity.ok(comptabiliteExtensionService.listerJournaux().stream().map(this::toJournalDto).toList());
    }

    @Operation(summary = "Lister les schemas comptables", description = "Retourne la liste de tous les schemas comptables")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des schemas retournee avec succes"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/schemas")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<List<SchemaComptableResponseDTO>> listerSchemas() {
        return ResponseEntity.ok(comptabiliteExtensionService.listerSchemas().stream().map(this::toSchemaDto).toList());
    }

    @Operation(summary = "Tester un schema comptable", description = "Simule un schema comptable et retourne les ecritures generees")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Test execute avec succes"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/schemas/test")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    @com.microfinance.core_banking.audit.AuditLog(action = "ACCOUNTING_SCHEMA_TEST", resource = "SCHEMA_COMPTABLE")
    public ResponseEntity<SchemaTestResponseDTO> testerSchema(@Valid @RequestBody TesterSchemaComptableRequestDTO dto) {
        return ResponseEntity.ok(comptabiliteExtensionService.testerSchemaComptable(dto));
    }

    @Operation(summary = "Lister les ecritures comptables", description = "Retourne les ecritures comptables filtrees par date et journal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des ecritures retournee avec succes"),
        @ApiResponse(responseCode = "400", description = "Parametres invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
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

    @Operation(summary = "Lister les pieces comptables", description = "Retourne les pieces comptables filtrees par date et journal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des pieces retournee avec succes"),
        @ApiResponse(responseCode = "400", description = "Parametres invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
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

    @Operation(summary = "Consulter le grand livre", description = "Retourne les lignes du grand livre pour un compte donne")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Grand livre retourne avec succes"),
        @ApiResponse(responseCode = "400", description = "Parametres invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse"),
        @ApiResponse(responseCode = "404", description = "Compte comptable introuvable")
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

    @Operation(summary = "Consulter la balance", description = "Retourne la balance des comptes pour une periode donnee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Balance retournee avec succes"),
        @ApiResponse(responseCode = "400", description = "Parametres invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/balance")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<List<BalanceLineDTO>> balance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin
    ) {
        return ResponseEntity.ok(comptabiliteExtensionService.consulterBalance(dateDebut, dateFin));
    }

    @Operation(summary = "Effectuer les controles comptables", description = "Execute les controles de coherence comptable pour une periode")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Controles executes avec succes"),
        @ApiResponse(responseCode = "400", description = "Parametres invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/controles")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ACCOUNTING_VIEW)")
    public ResponseEntity<ControlesComptablesResponseDTO> controles(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin
    ) {
        return ResponseEntity.ok(comptabiliteExtensionService.controlesComptables(dateDebut, dateFin));
    }

    @Operation(summary = "Lister les clotures comptables", description = "Retourne la liste des clotures comptables pour une periode")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des clotures retournee avec succes"),
        @ApiResponse(responseCode = "400", description = "Parametres invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
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
