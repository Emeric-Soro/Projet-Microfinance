package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.ConsentementOpenBanking;
import com.microfinance.core_banking.service.extension.OpenBankingService;
import com.microfinance.core_banking.service.security.SecurityConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/open-banking")
public class OpenBankingController {

    private final OpenBankingService openBankingService;

    public OpenBankingController(OpenBankingService openBankingService) {
        this.openBankingService = openBankingService;
    }

    @PostMapping("/consentements")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, "
            + "T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR)")
    public ResponseEntity<ConsentementOpenBanking> consentir(@RequestBody Map<String, Object> request) {
        Long partenaireId = Long.valueOf(request.get("idPartenaire").toString());
        Long clientId = Long.valueOf(request.get("idClient").toString());
        String type = (String) request.get("typeConsentement");
        String scope = (String) request.get("scope");
        return ResponseEntity.ok(openBankingService.consentir(partenaireId, clientId, type, scope));
    }

    @DeleteMapping("/consentements/{ref}")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, "
            + "T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR)")
    public ResponseEntity<Void> revoguer(@PathVariable String ref, @RequestBody Map<String, String> request) {
        openBankingService.revoguer(ref, request.get("motif"));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/consentements/{ref}/comptes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_VIEW_CLIENT, "
            + "T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    public ResponseEntity<List<Compte>> listerComptes(@PathVariable String ref) {
        return ResponseEntity.ok(openBankingService.listerComptesClient(ref));
    }

    @GetMapping("/consentements")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    public ResponseEntity<List<ConsentementOpenBanking>> listerConsentements(
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) Long clientId) {
        if (statut != null) {
            return ResponseEntity.ok(openBankingService.listerConsentementsParStatut(statut));
        }
        if (clientId != null) {
            return ResponseEntity.ok(openBankingService.listerConsentementsClient(clientId));
        }
        return ResponseEntity.ok(openBankingService.listerConsentementsParStatut("ACTIF"));
    }
}
