package com.microfinance.core_banking.api.controller.compte;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.response.compte.CarteVisaResponseDTO;
import com.microfinance.core_banking.entity.CarteVisa;
import com.microfinance.core_banking.mapper.CompteMapper;
import com.microfinance.core_banking.service.compte.CarteVisaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cartes-visa")
@Tag(name = "Cartes Visa", description = "API de gestion des cartes bancaires")
public class CarteVisaController {

	private final CarteVisaService carteVisaService;
	private final CompteMapper compteMapper;

	public CarteVisaController(CarteVisaService carteVisaService, CompteMapper compteMapper) {
		this.carteVisaService = carteVisaService;
		this.compteMapper = compteMapper;
	}

	@Operation(
			summary = "Commander une carte Visa",
			description = "Genere une nouvelle carte pour un compte existant"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Carte creee avec succes"),
			@ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    @AuditLog(action = "CARD_ORDER", resource = "CARTE_VISA")
    public ResponseEntity<CarteVisaResponseDTO> commanderCarte(@RequestParam String numCompte) {
		// Cree une nouvelle carte active rattachee au compte.
		CarteVisa carte = carteVisaService.commanderCarte(numCompte);
		return ResponseEntity.status(HttpStatus.CREATED).body(compteMapper.toCarteVisaResponseDTO(carte));
	}

	@Operation(
			summary = "Faire opposition",
			description = "Desactive une carte a partir de son numero"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Opposition appliquee avec succes"),
			@ApiResponse(responseCode = "404", description = "Carte introuvable")
    })
    @PutMapping("/{numeroCarte}/opposition")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    @AuditLog(action = "CARD_BLOCK", resource = "CARTE_VISA")
    public ResponseEntity<CarteVisaResponseDTO> faireOpposition(@PathVariable String numeroCarte) {
		// Desactive la carte pour empecher toute nouvelle utilisation.
		CarteVisa carte = carteVisaService.faireOpposition(numeroCarte);
		return ResponseEntity.ok(compteMapper.toCarteVisaResponseDTO(carte));
	}
}
