package com.soutra.microfinance.api.controller.audit;

import com.soutra.microfinance.dto.response.audit.SystemAuditLogResponseDTO;
import com.soutra.microfinance.entity.SystemAuditLog;
import com.soutra.microfinance.service.audit.SystemAuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/audit-logs")
@Tag(name = "Audit Logs", description = "API de consultation des logs d'audit avec filtres et tracabilite avant/apres")
public class AuditLogController {

    private final SystemAuditLogService systemAuditLogService;

    public AuditLogController(SystemAuditLogService systemAuditLogService) {
        this.systemAuditLogService = systemAuditLogService;
    }

    @Operation(
        summary = "Consulter les logs d'audit",
        description = "Retourne la liste paginee des logs d'audit avec filtres optionnels. " +
                      "Chaque log peut contenir les valeurs avant et apres modification (detailsAvant/detailsApres) " +
                      "ainsi que l'identifiant de l'entite concernee (idEntite)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logs d'audit retournes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    public ResponseEntity<Page<SystemAuditLogResponseDTO>> consulterLogs(
            @RequestParam(required = false) String utilisateur,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String ressource,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String idEntite,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime du,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime au,
            @ParameterObject Pageable pageable) {

        boolean hasFilters = (utilisateur != null && !utilisateur.isBlank())
                || (action     != null && !action.isBlank())
                || (ressource  != null && !ressource.isBlank())
                || (statut     != null && !statut.isBlank())
                || (idEntite   != null && !idEntite.isBlank())
                || du != null || au != null;

        Page<SystemAuditLog> logs = hasFilters
                ? systemAuditLogService.rechercherAvecFiltres(utilisateur, action, ressource, statut, idEntite, du, au, pageable)
                : systemAuditLogService.consulterTous(pageable);

        return ResponseEntity.ok(logs.map(this::toResponseDTO));
    }

    private SystemAuditLogResponseDTO toResponseDTO(SystemAuditLog log) {
        SystemAuditLogResponseDTO dto = new SystemAuditLogResponseDTO();
        dto.setId(log.getId());
        dto.setAction(log.getAction());
        dto.setResource(log.getRessource());
        dto.setUtilisateur(log.getUtilisateur());
        dto.setAdresseIp(log.getAdresseIp());
        dto.setDateAction(log.getDateAction());
        dto.setStatut(log.getStatut());
        dto.setMessageErreur(log.getMessageErreur());
        dto.setMethode(log.getMethode());
        dto.setIdEntite(log.getIdEntite());
        dto.setDetailsAvant(log.getDetailsAvant());
        dto.setDetailsApres(log.getDetailsApres());
        return dto;
    }
}
