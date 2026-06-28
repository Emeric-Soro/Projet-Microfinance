package com.soutra.microfinance.api.controller.dashboard;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.response.statistique.DashboardAgenceResponseDTO;
import com.soutra.microfinance.dto.response.statistique.DashboardDirectionResponseDTO;
import com.soutra.microfinance.dto.response.statistique.IndicateurTempsReelResponseDTO;
import com.soutra.microfinance.dto.response.statistique.DashboardChartsResponseDTO;
import com.soutra.microfinance.service.dashboard.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboards")
@Tag(name = "Dashboards", description = "API des tableaux de bord agence et direction")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(
            summary = "KPIs agence",
            description = "Retourne les indicateurs de performance d'une agence pour une periode donnee"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "KPIs agence recuperes avec succes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/agence")
    @PreAuthorize("hasAnyAuthority('ADMIN','CHEF_AGENCE','GUICHETIER')")
    @AuditLog(action = "DASHBOARD_AGENCE", resource = "DASHBOARD")
    public ResponseEntity<DashboardAgenceResponseDTO> getKpisAgence(
            @RequestParam(required = false) Long agenceId,
            @RequestParam(defaultValue = "JOUR") String periode
    ) {
        return ResponseEntity.ok(dashboardService.getKpisAgence(agenceId, periode));
    }

    @Operation(
            summary = "KPIs direction",
            description = "Retourne les indicateurs agreges pour la direction (reseau, totaux)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "KPIs direction recuperes avec succes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/direction")
    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR')")
    @AuditLog(action = "DASHBOARD_DIRECTION", resource = "DASHBOARD")
    public ResponseEntity<DashboardDirectionResponseDTO> getKpisDirection() {
        return ResponseEntity.ok(dashboardService.getKpisDirection());
    }

    @Operation(
            summary = "Indicateurs temps reel",
            description = "Retourne les indicateurs en temps reel (clients, comptes, transactions, sessions)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Indicateurs temps reel recuperes avec succes")
    })
    @GetMapping("/indicateurs")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<IndicateurTempsReelResponseDTO> getIndicateursTempsReel() {
        return ResponseEntity.ok(dashboardService.getIndicateursTempsReel());
    }

    @Operation(
            summary = "Donnees des graphiques",
            description = "Retourne la repartition des comptes et l'evolution de l'activite sur 7 jours"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donnees des graphiques recuperees avec succes")
    })
    @GetMapping("/graphiques")
    @PreAuthorize("hasAnyAuthority('ADMIN','CHEF_AGENCE','GUICHETIER','DIRECTEUR')")
    public ResponseEntity<DashboardChartsResponseDTO> getGraphiques(
            @RequestParam(required = false) Long agenceId
    ) {
        return ResponseEntity.ok(dashboardService.getGraphiques(agenceId));
    }
}
