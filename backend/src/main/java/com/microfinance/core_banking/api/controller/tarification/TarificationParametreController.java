package com.microfinance.core_banking.api.controller.tarification;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.service.tarification.TarificationParametreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tarification/parametres")
@Tag(name = "Parametres tarification", description = "API de gestion du cache de parametres de tarification")
public class TarificationParametreController {

    private final TarificationParametreService tarificationParametreService;

    public TarificationParametreController(TarificationParametreService tarificationParametreService) {
        this.tarificationParametreService = tarificationParametreService;
    }

    @Operation(
            summary = "Rafraichir le cache des parametres de tarification",
            description = "Vide le cache en memoire afin de recharger les valeurs depuis la base au prochain appel"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cache invalide avec succes")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/cache/refresh")
    @AuditLog(action = "TARIFF_CACHE_REFRESH", resource = "TARIFICATION_PARAMETRE")
    public ResponseEntity<Void> rafraichirCache() {
        tarificationParametreService.invaliderCache();
        return ResponseEntity.noContent().build();
    }
}
