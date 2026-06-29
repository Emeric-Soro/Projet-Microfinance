package com.soutra.microfinance.api.controller.reporting;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.parametrage.RapportPersonnaliseRequestDTO;
import com.soutra.microfinance.dto.response.common.*;
import com.soutra.microfinance.service.reporting.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reporting")
@Tag(name = "Reporting", description = "API des rapports operationnels, financiers et reglementaires")
public class ReportingController {

    private final ReportingService reportingService;

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @Operation(
            summary = "Rapport operationnel",
            description = "Retourne un rapport operationnel pour une periode et/ou agence donnees"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rapport operationnel genere avec succes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/operationnel")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','DIRECTEUR')")
    @AuditLog(action = "REPORTING_OPERATIONNEL", resource = "REPORTING")
    public ResponseEntity<RapportOperationnelResponseDTO> getRapportOperationnel(
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            @RequestParam(required = false) Long agenceId
    ) {
        return ResponseEntity.ok(reportingService.genererRapportOperationnel(dateDebut, dateFin, agenceId));
    }

    @Operation(
            summary = "Rapport financier",
            description = "Retourne un rapport financier avec les indicateurs cles"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rapport financier genere avec succes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/financier")
    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR')")
    @AuditLog(action = "REPORTING_FINANCIER", resource = "REPORTING")
    public ResponseEntity<RapportFinancierResponseDTO> getRapportFinancier(
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin
    ) {
        return ResponseEntity.ok(reportingService.genererRapportFinancier(dateDebut, dateFin));
    }

    @Operation(
            summary = "Rapport clients",
            description = "Retourne un rapport sur les clients avec repartition par statut"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rapport clients genere avec succes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/clients")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','DIRECTEUR')")
    @AuditLog(action = "REPORTING_CLIENTS", resource = "REPORTING")
    public ResponseEntity<RapportClientsResponseDTO> getRapportClients() {
        return ResponseEntity.ok(reportingService.genererRapportClients());
    }

    @Operation(
            summary = "Rapport credits",
            description = "Retourne un rapport sur les credits avec indicateurs de performance"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rapport credits genere avec succes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/credits")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','DIRECTEUR')")
    @AuditLog(action = "REPORTING_CREDITS", resource = "REPORTING")
    public ResponseEntity<RapportCreditsResponseDTO> getRapportCredits() {
        return ResponseEntity.ok(reportingService.genererRapportCredits());
    }

    @Operation(
            summary = "Rapport caisse",
            description = "Retourne un rapport sur les caisses et les mouvements"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rapport caisse genere avec succes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/caisse")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','DIRECTEUR')")
    @AuditLog(action = "REPORTING_CAISSE", resource = "REPORTING")
    public ResponseEntity<RapportCaisseResponseDTO> getRapportCaisse() {
        return ResponseEntity.ok(reportingService.genererRapportCaisse());
    }

    @Operation(
            summary = "Rapport BCEAO",
            description = "Retourne un rapport reglementaire BCEAO pour un trimestre et une annee donnes"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rapport BCEAO genere avec succes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/bceao")
    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR')")
    @AuditLog(action = "REPORTING_BCEAO", resource = "REPORTING")
    public ResponseEntity<RapportBceaoResponseDTO> getRapportBceao(
            @RequestParam(required = false, defaultValue = "1") int trimestre,
            @RequestParam(required = false) Integer annee
    ) {
        int anneeEffective = annee != null ? annee : java.time.LocalDate.now().getYear();
        return ResponseEntity.ok(reportingService.genererRapportBceao(trimestre, anneeEffective));
    }

    @Operation(
            summary = "Export rapport",
            description = "Exporte un rapport dans le format specifie (CSV/PDF)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rapport exporte avec succes"),
            @ApiResponse(responseCode = "400", description = "Type ou format invalide"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/export")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','DIRECTEUR')")
    @AuditLog(action = "REPORTING_EXPORT", resource = "REPORTING")
    public ResponseEntity<RapportExportResponseDTO> exportRapport(
            @RequestParam String type,
            @RequestParam(defaultValue = "CSV") String format,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin
    ) {
        return ResponseEntity.ok(reportingService.exporterRapport(type, format, dateDebut, dateFin));
    }

    @Operation(
            summary = "Rapport personnalise",
            description = "Genere un rapport personnalise selon les criteres fournis"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rapport personnalise en cours de generation"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/personnalise")
    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR')")
    @AuditLog(action = "REPORTING_PERSONNALISE", resource = "REPORTING")
    public ResponseEntity<RapportExportResponseDTO> genererRapportPersonnalise(
            @Valid @RequestBody RapportPersonnaliseRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(reportingService.genererRapportPersonnalise(requestDTO));
    }
}
