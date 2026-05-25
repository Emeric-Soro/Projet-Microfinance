package com.soutra.microfinance.api.controller.parametrage;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.entity.Agence;
import com.soutra.microfinance.service.parametrage.AgenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/agences")
@Tag(name = "Agences", description = "API de gestion des agences (Systeme Decentralise)")
public class AgenceController {

	private final AgenceService agenceService;

	public AgenceController(AgenceService agenceService) {
		this.agenceService = agenceService;
	}

	@Operation(
			summary = "Creer une agence",
			description = "Enregistre un nouveau point de service"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Agence creee avec succes"),
			@ApiResponse(responseCode = "400", description = "Donnees invalides"),
			@ApiResponse(responseCode = "409", description = "Code agence deja utilise")
	})
	@PostMapping
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@AuditLog(action = "AGENCY_CREATE", resource = "AGENCE")
	public ResponseEntity<Agence> creerAgence(@Valid @RequestBody Agence agence) {
		return ResponseEntity.status(HttpStatus.CREATED).body(agenceService.creerAgence(agence));
	}

	@Operation(
			summary = "Modifier une agence",
			description = "Met a jour les informations d'une agence existante"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Agence modifiee avec succes"),
			@ApiResponse(responseCode = "404", description = "Agence introuvable")
	})
	@PutMapping("/{idAgence}")
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@AuditLog(action = "AGENCY_UPDATE", resource = "AGENCE")
	public ResponseEntity<Agence> modifierAgence(
			@PathVariable Long idAgence,
			@Valid @RequestBody Agence modifications
	) {
		return ResponseEntity.ok(agenceService.modifierAgence(idAgence, modifications));
	}

	@Operation(
			summary = "Consulter une agence",
			description = "Retourne les details d'une agence"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Agence trouvee"),
			@ApiResponse(responseCode = "404", description = "Agence introuvable")
	})
	@GetMapping("/{idAgence}")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE','GUICHETIER')")
	public ResponseEntity<Agence> obtenirAgence(@PathVariable Long idAgence) {
		return ResponseEntity.ok(agenceService.obtenirAgence(idAgence));
	}

	@Operation(
			summary = "Lister les agences actives",
			description = "Retourne toutes les agences actuellement en activite"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste des agences actives")
	})
	@GetMapping
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE','GUICHETIER')")
	public ResponseEntity<List<Agence>> listerAgencesActives() {
		return ResponseEntity.ok(agenceService.listerAgencesActives());
	}

	@Operation(
			summary = "Desactiver une agence",
			description = "Desactive une agence sans la supprimer physiquement"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Agence desactivee avec succes"),
			@ApiResponse(responseCode = "404", description = "Agence introuvable")
	})
	@PutMapping("/{idAgence}/desactiver")
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@AuditLog(action = "AGENCY_DEACTIVATE", resource = "AGENCE")
	public ResponseEntity<Agence> desactiverAgence(@PathVariable Long idAgence) {
		return ResponseEntity.ok(agenceService.desactiverAgence(idAgence));
	}
}
