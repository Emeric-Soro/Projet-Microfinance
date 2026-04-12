package com.microfinance.core_banking.api.controller.tarification;

import com.microfinance.core_banking.dto.response.tarification.AgioResponseDTO;
import com.microfinance.core_banking.entity.Agio;
import com.microfinance.core_banking.mapper.TarificationMapper;
import com.microfinance.core_banking.service.tarification.AgioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/agios")
@Tag(name = "Agios", description = "API de calcul et prelevement des frais")
public class AgioController {

	private final AgioService agioService;
	private final TarificationMapper tarificationMapper;

	public AgioController(AgioService agioService, TarificationMapper tarificationMapper) {
		this.agioService = agioService;
		this.tarificationMapper = tarificationMapper;
	}

	@Operation(
			summary = "Calculer les frais mensuels",
			description = "Calcule les frais de tenue de compte pour l'ensemble des comptes"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Frais calcules avec succes"),
			@ApiResponse(responseCode = "409", description = "Conflit metier ou parametrage manquant")
	})
	@PostMapping("/frais-tenue/mensuel")
	public ResponseEntity<List<AgioResponseDTO>> calculerFraisTenueCompteMensuel() {
		// Lance le calcul batch des frais mensuels.
		List<AgioResponseDTO> resultats = agioService.calculerFraisTenueCompteMensuel().stream()
				.map(tarificationMapper::toAgioResponseDTO)
				.toList();
		return ResponseEntity.ok(resultats);
	}

	@Operation(
			summary = "Calculer la penalite de decouvert",
			description = "Calcule la penalite d'un compte a decouvert si applicable"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Penalite calculee ou deja existante"),
			@ApiResponse(responseCode = "204", description = "Aucune penalite a appliquer"),
			@ApiResponse(responseCode = "404", description = "Compte introuvable")
	})
	@PostMapping("/penalite-decouvert")
	public ResponseEntity<AgioResponseDTO> calculerPenaliteDecouvert(@RequestParam String numCompte) {
		// Retourne 204 si le compte n'est pas en decouvert.
		return agioService.calculerPenaliteDecouvert(numCompte)
				.map(tarificationMapper::toAgioResponseDTO)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.noContent().build());
	}

	@Operation(
			summary = "Executer les prelevements d'agios",
			description = "Tente de prelever les agios en attente avec un utilisateur systeme"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Prelevements executes"),
			@ApiResponse(responseCode = "400", description = "Parametres invalides"),
			@ApiResponse(responseCode = "404", description = "Utilisateur systeme introuvable")
	})
	@PostMapping("/prelevements")
	public ResponseEntity<List<AgioResponseDTO>> executerPrelevementsEnAttente(@RequestParam Long idUserSysteme) {
		// Tente le prelevement de chaque agio non preleve.
		List<Agio> preleves = agioService.executerPrelevementsEnAttente(idUserSysteme);
		List<AgioResponseDTO> response = preleves.stream()
				.map(tarificationMapper::toAgioResponseDTO)
				.toList();
		return ResponseEntity.ok(response);
	}
}
