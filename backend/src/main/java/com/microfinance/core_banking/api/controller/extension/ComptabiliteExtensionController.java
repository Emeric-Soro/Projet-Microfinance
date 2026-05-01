package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.ClasseComptable;
import com.microfinance.core_banking.entity.ClotureComptable;
import com.microfinance.core_banking.entity.CompteComptable;
import com.microfinance.core_banking.entity.EcritureComptable;
import com.microfinance.core_banking.entity.JournalComptable;
import com.microfinance.core_banking.entity.SchemaComptable;
import com.microfinance.core_banking.service.extension.ComptabiliteExtensionService;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/bootstrap")
    @PreAuthorize("hasAnyAuthority('ADMIN','ACCOUNTING_MANAGE')")
    @AuditLog(action = "ACCOUNTING_BOOTSTRAP", resource = "COMPTABILITE")
    public ResponseEntity<Map<String, Object>> bootstrap() {
        return ResponseEntity.ok(comptabiliteExtensionService.bootstrapReferentiel());
    }

    @PostMapping("/classes")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','ACCOUNTING_MANAGE')")
    @AuditLog(action = "ACCOUNTING_CLASS_CREATE", resource = "CLASSE_COMPTABLE")
    public ResponseEntity<Map<String, Object>> creerClasse(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_CLASSE_COMPTABLE", "CLASSE_COMPTABLE", (String) payload.get("codeClasse"), payload, "Creation classe comptable soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @PostMapping("/comptes")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','ACCOUNTING_MANAGE')")
    @AuditLog(action = "ACCOUNTING_ACCOUNT_CREATE", resource = "COMPTE_COMPTABLE")
    public ResponseEntity<Map<String, Object>> creerCompte(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_COMPTE_COMPTABLE", "COMPTE_COMPTABLE", (String) payload.get("numeroCompte"), payload, "Creation compte comptable soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @PostMapping("/journaux")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','ACCOUNTING_MANAGE')")
    @AuditLog(action = "ACCOUNTING_JOURNAL_CREATE", resource = "JOURNAL_COMPTABLE")
    public ResponseEntity<Map<String, Object>> creerJournal(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_JOURNAL_COMPTABLE", "JOURNAL_COMPTABLE", (String) payload.get("codeJournal"), payload, "Creation journal comptable soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @PostMapping("/schemas")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','ACCOUNTING_MANAGE')")
    @AuditLog(action = "ACCOUNTING_SCHEMA_CREATE", resource = "SCHEMA_COMPTABLE")
    public ResponseEntity<Map<String, Object>> creerSchema(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_SCHEMA_COMPTABLE", "SCHEMA_COMPTABLE", (String) payload.get("codeOperation"), payload, "Creation schema comptable soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @PostMapping("/ecritures")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','ACCOUNTING_MANAGE')")
    @AuditLog(action = "ACCOUNTING_MANUAL_ENTRY_CREATE", resource = "ECRITURE_COMPTABLE")
    public ResponseEntity<Map<String, Object>> creerEcriture(@RequestBody Map<String, Object> payload) {
        String referencePiece = payload.get("referencePiece") == null ? null : payload.get("referencePiece").toString();
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_ECRITURE_MANUELLE", "ECRITURE_COMPTABLE", referencePiece, payload, "Ecriture manuelle soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @PostMapping("/clotures")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','ACCOUNTING_MANAGE')")
    @AuditLog(action = "ACCOUNTING_CLOSE", resource = "CLOTURE_COMPTABLE")
    public ResponseEntity<Map<String, Object>> cloturer(@RequestBody Map<String, Object> payload) {
        String referenceCloture = payload.get("dateFin") == null ? null : payload.get("dateFin").toString();
        ActionEnAttente action = pendingActionSubmissionService.submit("CLOTURE_COMPTABLE", "CLOTURE_COMPTABLE", referenceCloture, payload, "Cloture comptable soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/classes")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ACCOUNTING_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerClasses() {
        return ResponseEntity.ok(comptabiliteExtensionService.listerClasses().stream().map(this::toClasseMap).toList());
    }

    @GetMapping("/comptes")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ACCOUNTING_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerComptes() {
        return ResponseEntity.ok(comptabiliteExtensionService.listerComptes().stream().map(this::toCompteMap).toList());
    }

    @GetMapping("/journaux")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ACCOUNTING_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerJournaux() {
        return ResponseEntity.ok(comptabiliteExtensionService.listerJournaux().stream().map(this::toJournalMap).toList());
    }

    @GetMapping("/schemas")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ACCOUNTING_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerSchemas() {
        return ResponseEntity.ok(comptabiliteExtensionService.listerSchemas().stream().map(this::toSchemaMap).toList());
    }

    @PostMapping("/schemas/test")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ACCOUNTING_VIEW')")
    @AuditLog(action = "ACCOUNTING_SCHEMA_TEST", resource = "SCHEMA_COMPTABLE")
    public ResponseEntity<Map<String, Object>> testerSchema(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(comptabiliteExtensionService.testerSchemaComptable(payload));
    }

    @GetMapping("/ecritures")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ACCOUNTING_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerEcritures(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) String codeJournal
    ) {
        return ResponseEntity.ok(comptabiliteExtensionService.listerEcritures(dateDebut, dateFin, codeJournal).stream().map(this::toEcritureMap).toList());
    }

    @GetMapping("/pieces")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ACCOUNTING_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerPieces(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) String codeJournal
    ) {
        return ResponseEntity.ok(comptabiliteExtensionService.listerEcritures(dateDebut, dateFin, codeJournal).stream().map(this::toEcritureMap).toList());
    }

    @GetMapping("/grand-livre")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ACCOUNTING_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> grandLivre(
            @RequestParam String numeroCompte,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin
    ) {
        return ResponseEntity.ok(comptabiliteExtensionService.consulterGrandLivre(numeroCompte, dateDebut, dateFin));
    }

    @GetMapping("/balance")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ACCOUNTING_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> balance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin
    ) {
        return ResponseEntity.ok(comptabiliteExtensionService.consulterBalance(dateDebut, dateFin));
    }

    @GetMapping("/controles")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ACCOUNTING_VIEW')")
    public ResponseEntity<Map<String, Object>> controles(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin
    ) {
        return ResponseEntity.ok(comptabiliteExtensionService.controlesComptables(dateDebut, dateFin));
    }

    @GetMapping("/clotures")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ACCOUNTING_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerClotures(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin
    ) {
        return ResponseEntity.ok(comptabiliteExtensionService.listerClotures(dateDebut, dateFin).stream().map(this::toClotureMap).toList());
    }

    private Map<String, Object> toClasseMap(ClasseComptable classe) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idClasseComptable", classe.getIdClasseComptable());
        response.put("codeClasse", classe.getCodeClasse());
        response.put("libelle", classe.getLibelle());
        response.put("ordreAffichage", classe.getOrdreAffichage());
        return response;
    }

    private Map<String, Object> toCompteMap(CompteComptable compte) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idCompteComptable", compte.getIdCompteComptable());
        response.put("numeroCompte", compte.getNumeroCompte());
        response.put("intitule", compte.getIntitule());
        response.put("typeSolde", compte.getTypeSolde());
        response.put("compteInterne", compte.getCompteInterne());
        response.put("classe", compte.getClasseComptable() == null ? null : compte.getClasseComptable().getCodeClasse());
        response.put("agence", compte.getAgence() == null ? null : compte.getAgence().getNomAgence());
        return response;
    }

    private Map<String, Object> toJournalMap(JournalComptable journal) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idJournalComptable", journal.getIdJournalComptable());
        response.put("codeJournal", journal.getCodeJournal());
        response.put("libelle", journal.getLibelle());
        response.put("typeJournal", journal.getTypeJournal());
        response.put("actif", journal.getActif());
        return response;
    }

    private Map<String, Object> toSchemaMap(SchemaComptable schema) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idSchemaComptable", schema.getIdSchemaComptable());
        response.put("codeOperation", schema.getCodeOperation());
        response.put("compteDebit", schema.getCompteDebit());
        response.put("compteCredit", schema.getCompteCredit());
        response.put("compteFrais", schema.getCompteFrais());
        response.put("journalCode", schema.getJournalCode());
        response.put("actif", schema.getActif());
        return response;
    }

    private Map<String, Object> toEcritureMap(EcritureComptable ecriture) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idEcritureComptable", ecriture.getIdEcritureComptable());
        response.put("referencePiece", ecriture.getReferencePiece());
        response.put("journal", ecriture.getJournalComptable().getCodeJournal());
        response.put("dateComptable", ecriture.getDateComptable());
        response.put("dateValeur", ecriture.getDateValeur());
        response.put("libelle", ecriture.getLibelle());
        response.put("sourceType", ecriture.getSourceType());
        response.put("sourceReference", ecriture.getSourceReference());
        response.put("statut", ecriture.getStatut());
        return response;
    }

    private Map<String, Object> toClotureMap(ClotureComptable cloture) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idClotureComptable", cloture.getIdClotureComptable());
        response.put("typeCloture", cloture.getTypeCloture());
        response.put("dateDebut", cloture.getDateDebut());
        response.put("dateFin", cloture.getDateFin());
        response.put("totalEcritures", cloture.getTotalEcritures());
        response.put("statut", cloture.getStatut());
        response.put("commentaire", cloture.getCommentaire());
        return response;
    }

    private Map<String, Object> toActionMap(ActionEnAttente action) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idActionEnAttente", action.getIdActionEnAttente());
        response.put("typeAction", action.getTypeAction());
        response.put("ressource", action.getRessource());
        response.put("referenceRessource", action.getReferenceRessource());
        response.put("statut", action.getStatut());
        return response;
    }
}
