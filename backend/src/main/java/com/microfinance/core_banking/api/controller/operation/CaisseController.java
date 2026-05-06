package com.microfinance.core_banking.api.controller.operation;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.operation.FermetureCaisseRequestDTO;
import com.microfinance.core_banking.dto.request.operation.OuvertureCaisseRequestDTO;
import com.microfinance.core_banking.entity.Caisse;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.service.operation.CaisseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/caisses")
@Tag(name = "Caisses", description = "Gestion des tiroirs-caisses guichetier")
public class CaisseController {

    private final CaisseService caisseService;

    public CaisseController(CaisseService caisseService) {
        this.caisseService = caisseService;
    }

    @Operation(summary = "Ouvrir une caisse", description = "Ouvre un tiroir-caisse pour le guichetier connecte")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Caisse ouverte avec succes"),
            @ApiResponse(responseCode = "400", description = "Caisse deja ouverte"),
            @ApiResponse(responseCode = "404", description = "Guichetier introuvable")
    })
    @PostMapping("/ouverture")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "CAISSE_OPEN", resource = "CAISSE")
    public ResponseEntity<Caisse> ouvrirCaisse(
            @Valid @RequestBody OuvertureCaisseRequestDTO dto,
            Authentication authentication
    ) {
        Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();
        Caisse caisse = caisseService.ouvrirCaisse(utilisateur.getIdUser(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(caisse);
    }

    @Operation(summary = "Fermer une caisse", description = "Ferme la caisse ouverte et calcule l'ecart entre le solde informatique et le solde physique")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Caisse fermee avec succes"),
            @ApiResponse(responseCode = "400", description = "Aucune caisse ouverte a fermer")
    })
    @PostMapping("/fermeture")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "CAISSE_CLOSE", resource = "CAISSE")
    public ResponseEntity<Caisse> fermerCaisse(
            @Valid @RequestBody FermetureCaisseRequestDTO dto,
            Authentication authentication
    ) {
        Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();
        Caisse caisse = caisseService.fermerCaisse(utilisateur.getIdUser(), dto);
        return ResponseEntity.ok(caisse);
    }

    @Operation(summary = "Consulter l'etat de la caisse", description = "Retourne le solde actuel de la caisse du guichetier connecte")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Etat de la caisse"),
            @ApiResponse(responseCode = "400", description = "Aucune caisse ouverte")
    })
    @GetMapping("/etat")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    public ResponseEntity<Caisse> etatCaisse(Authentication authentication) {
        Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();
        Caisse caisse = caisseService.consulterCaisseOuverte(utilisateur.getIdUser());
        return ResponseEntity.ok(caisse);
    }
}
