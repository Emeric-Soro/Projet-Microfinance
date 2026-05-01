package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.AppareilClient;
import com.microfinance.core_banking.entity.Employe;
import com.microfinance.core_banking.entity.PartenaireApi;
import com.microfinance.core_banking.service.extension.DigitalExtensionService;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/extensions")
public class DigitalExtensionController {

    private final DigitalExtensionService digitalExtensionService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public DigitalExtensionController(DigitalExtensionService digitalExtensionService, PendingActionSubmissionService pendingActionSubmissionService) {
        this.digitalExtensionService = digitalExtensionService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @PostMapping("/appareils-clients")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','DIGITAL_MANAGE')")
    @AuditLog(action = "CLIENT_DEVICE_REGISTER", resource = "APPAREIL_CLIENT")
    public ResponseEntity<Map<String, Object>> enregistrerAppareil(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toAppareilMap(digitalExtensionService.enregistrerAppareil(payload)));
    }

    @GetMapping("/appareils-clients")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','DIGITAL_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerAppareils() {
        return ResponseEntity.ok(digitalExtensionService.listerAppareils().stream().map(this::toAppareilMap).toList());
    }

    @PostMapping("/partenaires-api")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','DIGITAL_MANAGE')")
    @AuditLog(action = "API_PARTNER_CREATE", resource = "PARTENAIRE_API")
    public ResponseEntity<Map<String, Object>> creerPartenaire(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_PARTENAIRE_API", "PARTENAIRE_API", (String) payload.get("codePartenaire"), payload, "Creation partenaire API soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/partenaires-api")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','DIGITAL_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerPartenaires() {
        return ResponseEntity.ok(digitalExtensionService.listerPartenaires().stream().map(this::toPartenaireMap).toList());
    }

    @PostMapping("/employes")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','DIGITAL_MANAGE')")
    @AuditLog(action = "EMPLOYEE_CREATE", resource = "EMPLOYE")
    public ResponseEntity<Map<String, Object>> creerEmploye(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_EMPLOYE", "EMPLOYE", payload.get("matricule") == null ? null : payload.get("matricule").toString(), payload, "Creation employe soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/employes")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','DIGITAL_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerEmployes() {
        return ResponseEntity.ok(digitalExtensionService.listerEmployes().stream().map(this::toEmployeMap).toList());
    }

    private Map<String, Object> toAppareilMap(AppareilClient appareil) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idAppareilClient", appareil.getIdAppareilClient());
        response.put("idClient", appareil.getClient().getIdClient());
        response.put("empreinteAppareil", appareil.getEmpreinteAppareil());
        response.put("plateforme", appareil.getPlateforme());
        response.put("nomAppareil", appareil.getNomAppareil());
        response.put("autorise", appareil.getAutorise());
        response.put("derniereConnexion", appareil.getDerniereConnexion());
        return response;
    }

    private Map<String, Object> toPartenaireMap(PartenaireApi partenaire) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idPartenaireApi", partenaire.getIdPartenaireApi());
        response.put("codePartenaire", partenaire.getCodePartenaire());
        response.put("nomPartenaire", partenaire.getNomPartenaire());
        response.put("typePartenaire", partenaire.getTypePartenaire());
        response.put("webhookUrl", partenaire.getWebhookUrl());
        response.put("statut", partenaire.getStatut());
        response.put("quotasJournaliers", partenaire.getQuotasJournaliers());
        return response;
    }

    private Map<String, Object> toEmployeMap(Employe employe) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idEmploye", employe.getIdEmploye());
        response.put("matricule", employe.getMatricule());
        response.put("nomComplet", employe.getNomComplet());
        response.put("poste", employe.getPoste());
        response.put("service", employe.getService());
        response.put("statut", employe.getStatut());
        response.put("agence", employe.getAgence() == null ? null : employe.getAgence().getNomAgence());
        return response;
    }

    private Map<String, Object> toActionMap(ActionEnAttente action) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idActionEnAttente", action.getIdActionEnAttente());
        response.put("typeAction", action.getTypeAction());
        response.put("ressource", action.getRessource());
        response.put("statut", action.getStatut());
        return response;
    }
}
