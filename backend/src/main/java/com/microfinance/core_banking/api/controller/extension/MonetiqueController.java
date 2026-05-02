package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.entity.CarteVisa;
import com.microfinance.core_banking.service.extension.MonetiqueService;
import com.microfinance.core_banking.service.security.SecurityConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/monetique")
public class MonetiqueController {

    private final MonetiqueService monetiqueService;

    public MonetiqueController(MonetiqueService monetiqueService) {
        this.monetiqueService = monetiqueService;
    }

    @PostMapping("/cartes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREATE_CARTE, "
            + "T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    public ResponseEntity<CarteVisa> emettreCarte(@RequestBody Map<String, Object> request) {
        Long idCompte = Long.valueOf(request.get("idCompte").toString());
        String typeCarte = (String) request.get("typeCarte");
        BigDecimal plafondJournalier = request.get("plafondJournalier") != null
                ? new BigDecimal(request.get("plafondJournalier").toString()) : null;
        BigDecimal plafondMensuel = request.get("plafondMensuel") != null
                ? new BigDecimal(request.get("plafondMensuel").toString()) : null;
        return ResponseEntity.ok(monetiqueService.emettreCarte(idCompte, typeCarte, plafondJournalier, plafondMensuel));
    }

    @PostMapping("/cartes/{id}/pin")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_UPDATE_CARTE, "
            + "T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    public ResponseEntity<Void> definirPin(@PathVariable Long id, @RequestBody Map<String, String> request) {
        monetiqueService.definirPin(id, request.get("pin"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/paiements/pos")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_PAYMENT_CARTE, "
            + "T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    public ResponseEntity<Map<String, Object>> paiementPos(@RequestBody Map<String, Object> request) {
        Long idCarte = Long.valueOf(request.get("idCarte").toString());
        String pin = (String) request.get("pin");
        if (!monetiqueService.verifierPin(idCarte, pin)) {
            return ResponseEntity.status(403).body(Map.of("erreur", "PIN invalide ou carte bloquee"));
        }
        return ResponseEntity.ok(Map.of("statut", "OK", "message", "Paiement autorise"));
    }

    @PutMapping("/cartes/{id}/blocage")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_UPDATE_CARTE, "
            + "T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    public ResponseEntity<CarteVisa> bloquerCarte(@PathVariable Long id, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(monetiqueService.bloquerCarte(id, request.get("motif")));
    }

    @PutMapping("/cartes/{id}/deblocage")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_UPDATE_CARTE, "
            + "T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    public ResponseEntity<CarteVisa> debloquerCarte(@PathVariable Long id) {
        return ResponseEntity.ok(monetiqueService.debloquerCarte(id));
    }

    @PostMapping("/cartes/{id}/token")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    public ResponseEntity<Map<String, String>> tokeniserCarte(@PathVariable Long id) {
        String token = monetiqueService.tokeniserCarte(id);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
