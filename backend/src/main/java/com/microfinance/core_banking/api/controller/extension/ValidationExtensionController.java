package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.ValidationExtensionService;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/validations")
public class ValidationExtensionController {

    private final ValidationExtensionService validationExtensionService;

    public ValidationExtensionController(ValidationExtensionService validationExtensionService) {
        this.validationExtensionService = validationExtensionService;
    }

    @PostMapping("/actions-en-attente")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','VALIDATION_VIEW')")
    @AuditLog(action = "PENDING_ACTION_CREATE", resource = "ACTION_EN_ATTENTE")
    public ResponseEntity<Map<String, Object>> creerAction(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toActionMap(validationExtensionService.creerAction(payload)));
    }

    @PutMapping("/actions-en-attente/{idAction}/decision")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','VALIDATION_DECIDE')")
    @AuditLog(action = "PENDING_ACTION_DECISION", resource = "ACTION_EN_ATTENTE")
    public ResponseEntity<Map<String, Object>> validerAction(@PathVariable Long idAction, @RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(toActionMap(validationExtensionService.validerAction(idAction, payload)));
    }

    @GetMapping("/actions-en-attente")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','VALIDATION_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerActions() {
        return ResponseEntity.ok(validationExtensionService.listerActions().stream().map(this::toActionMap).toList());
    }

    private Map<String, Object> toActionMap(ActionEnAttente action) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idActionEnAttente", action.getIdActionEnAttente());
        response.put("typeAction", action.getTypeAction());
        response.put("ressource", action.getRessource());
        response.put("referenceRessource", action.getReferenceRessource());
        response.put("statut", action.getStatut());
        response.put("maker", action.getMaker().getIdUser());
        response.put("checker", action.getChecker() == null ? null : action.getChecker().getIdUser());
        response.put("referenceRessource", action.getReferenceRessource());
        response.put("dateValidation", action.getDateValidation());
        return response;
    }
}
