package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.CreerActionRequestDTO;
import com.microfinance.core_banking.dto.request.extension.ValiderActionRequestDTO;
import com.microfinance.core_banking.dto.response.extension.ActionEnAttenteResponseDTO;
import com.microfinance.core_banking.service.extension.ValidationExtensionService;
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
@RequestMapping("/api/validations")
public class ValidationExtensionController {

    private final ValidationExtensionService validationExtensionService;

    public ValidationExtensionController(ValidationExtensionService validationExtensionService) {
        this.validationExtensionService = validationExtensionService;
    }

    @PostMapping("/actions-en-attente")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_VALIDATION_VIEW)")
    @AuditLog(action = "PENDING_ACTION_CREATE", resource = "ACTION_EN_ATTENTE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerAction(@Valid @RequestBody CreerActionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toActionDto(validationExtensionService.creerAction(dto)));
    }

    @PutMapping("/actions-en-attente/{idAction}/decision")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_VALIDATION_DECIDE)")
    @AuditLog(action = "PENDING_ACTION_DECISION", resource = "ACTION_EN_ATTENTE")
    public ResponseEntity<ActionEnAttenteResponseDTO> validerAction(@PathVariable Long idAction, @Valid @RequestBody ValiderActionRequestDTO dto) {
        return ResponseEntity.ok(toActionDto(validationExtensionService.validerAction(idAction, dto)));
    }

    @GetMapping("/actions-en-attente")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_VALIDATION_VIEW)")
    public ResponseEntity<List<ActionEnAttenteResponseDTO>> listerActions() {
        return ResponseEntity.ok(validationExtensionService.listerActions().stream().map(this::toActionDto).toList());
    }

    private ActionEnAttenteResponseDTO toActionDto(com.microfinance.core_banking.entity.ActionEnAttente action) {
        ActionEnAttenteResponseDTO dto = new ActionEnAttenteResponseDTO();
        dto.setIdActionEnAttente(action.getIdActionEnAttente());
        dto.setTypeAction(action.getTypeAction());
        dto.setRessource(action.getRessource());
        dto.setReferenceRessource(action.getReferenceRessource());
        dto.setStatut(action.getStatut());
        return dto;
    }
}
