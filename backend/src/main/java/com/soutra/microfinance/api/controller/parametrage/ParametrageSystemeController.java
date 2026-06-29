package com.soutra.microfinance.api.controller.parametrage;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.parametrage.JourFerieRequestDTO;
import com.soutra.microfinance.dto.response.parametrage.JourFerieResponseDTO;
import com.soutra.microfinance.dto.response.parametrage.ParametreSystemeResponseDTO;
import com.soutra.microfinance.service.parametrage.ParametrageSystemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/parametrages")
@Tag(name = "Parametrage Systeme", description = "API de parametrage systeme (configuration generale, jours feries)")
public class ParametrageSystemeController {

    private final ParametrageSystemeService parametrageSystemeService;

    public ParametrageSystemeController(ParametrageSystemeService parametrageSystemeService) {
        this.parametrageSystemeService = parametrageSystemeService;
    }

    @Operation(summary = "Consulter les parametres systeme",
            description = "Retourne la configuration generale du systeme (devise, seuils, delais)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Parametres systeme retournes avec succes")
    })
    @GetMapping("/systeme")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ParametreSystemeResponseDTO>> consulterParametresSysteme() {
        Map<String, String> params = parametrageSystemeService.consulterParametres();
        AtomicLong index = new AtomicLong(1);

        List<ParametreSystemeResponseDTO> dtos = params.entrySet().stream()
                .map(entry -> new ParametreSystemeResponseDTO(
                        index.getAndIncrement(),
                        entry.getKey(),
                        entry.getKey(),
                        entry.getValue(),
                        "STRING",
                        null,
                        true
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Mettre a jour les parametres systeme",
            description = "Met a jour la configuration generale du systeme (devise, timezone, timeouts)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Parametres systeme mis a jour avec succes")
    })
    @PutMapping("/systeme")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AuditLog(action = "PARAM_UPDATE_SYSTEM", resource = "PARAMETRAGE")
    public ResponseEntity<List<ParametreSystemeResponseDTO>> mettreAJourParametresSysteme(
            @RequestBody Map<String, Object> params) {

        params.forEach((key, value) -> {
            String dbCode = switch (key) {
                case "devise" -> "DEVISE_DEFAULT";
                case "timezone" -> "TIMEZONE";
                case "sessionTimeout" -> "SESSION_TIMEOUT_MIN";
                case "sessionAlert" -> "SESSION_ALERT_MIN";
                default -> key;
            };
            parametrageSystemeService.mettreAJourParametre(dbCode, String.valueOf(value));
            if ("devise".equals(key)) {
                // Also update the general DEVISE parameter to keep them in sync
                parametrageSystemeService.mettreAJourParametre("DEVISE", String.valueOf(value));
            }
        });

        return consulterParametresSysteme();
    }

    @Operation(summary = "Mettre a jour les jours feries",
            description = "Remplace la liste complete des jours feries")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jours feries mis a jour avec succes")
    })
    @PutMapping("/jours-feries")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AuditLog(action = "PARAM_UPDATE_JOURS_FERIES", resource = "PARAMETRAGE")
    public ResponseEntity<List<JourFerieResponseDTO>> mettreAJourJoursFeries(
            @Valid @RequestBody List<JourFerieRequestDTO> joursFeries) {

        List<JourFerieResponseDTO> misAJour = parametrageSystemeService.mettreAJourJoursFeries(joursFeries);
        return ResponseEntity.ok(misAJour);
    }

	@Operation(summary = "Lister les jours feries", description = "Retourne la liste des jours feries configures")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste des jours feries retournee avec succes")
	})
	@GetMapping("/jours-feries")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<List<JourFerieResponseDTO>> listerJoursFeries() {
        return ResponseEntity.ok(parametrageSystemeService.listerJoursFeries());
    }
}
