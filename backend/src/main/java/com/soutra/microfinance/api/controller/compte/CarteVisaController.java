package com.soutra.microfinance.api.controller.compte;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.compte.CarteVisaPatchRequestDTO;
import com.soutra.microfinance.dto.response.compte.CarteVisaDetailResponseDTO;
import com.soutra.microfinance.dto.response.compte.CarteVisaResponseDTO;
import com.soutra.microfinance.entity.CarteVisa;
import com.soutra.microfinance.mapper.CompteMapper;
import com.soutra.microfinance.service.compte.CarteVisaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cartes-visa")
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
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
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
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "CARD_BLOCK", resource = "CARTE_VISA")
    public ResponseEntity<CarteVisaResponseDTO> faireOpposition(@PathVariable String numeroCarte) {
		// Desactive la carte pour empecher toute nouvelle utilisation.
		CarteVisa carte = carteVisaService.faireOpposition(numeroCarte);
		return ResponseEntity.ok(compteMapper.toCarteVisaResponseDTO(carte));
	}

    @Operation(
            summary = "Lister les cartes d'un compte",
            description = "Retourne la liste paginee des cartes rattachees a un compte"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des cartes"),
            @ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR')")
    @AuditLog(action = "CARD_LIST", resource = "CARTE_VISA")
    public ResponseEntity<Page<CarteVisaResponseDTO>> listerCartes(
            @RequestParam String numCompte,
            Pageable pageable
    ) {
        Page<CarteVisa> cartes = carteVisaService.listerCartesParCompte(numCompte, pageable);
        return ResponseEntity.ok(cartes.map(compteMapper::toCarteVisaResponseDTO));
    }

    @Operation(
            summary = "Detail d'une carte Visa",
            description = "Retourne les informations d'une carte. Le CVV et le PIN ne sont JAMAIS exposes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detail de la carte"),
            @ApiResponse(responseCode = "404", description = "Carte introuvable")
    })
    @GetMapping("/{numeroCarte}")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR')")
    @AuditLog(action = "CARD_VIEW", resource = "CARTE_VISA")
    public ResponseEntity<CarteVisaDetailResponseDTO> obtenirCarte(@PathVariable String numeroCarte) {
        CarteVisa carte = carteVisaService.obtenirCarte(numeroCarte);
        return ResponseEntity.ok(toDetailDTO(carte));
    }

    @Operation(
            summary = "Modifier partiellement une carte",
            description = "PATCH partiel : seuls les champs non null sont appliques. CVV et PIN jamais exposes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carte mise a jour"),
            @ApiResponse(responseCode = "404", description = "Carte introuvable")
    })
    @PatchMapping("/{numeroCarte}")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "CARD_UPDATE", resource = "CARTE_VISA")
    public ResponseEntity<CarteVisaDetailResponseDTO> modifierCarte(
            @PathVariable String numeroCarte,
            @RequestBody CarteVisaPatchRequestDTO patch
    ) {
        CarteVisa carte = carteVisaService.modifierPartiellement(numeroCarte, patch);
        return ResponseEntity.ok(toDetailDTO(carte));
    }

    private CarteVisaDetailResponseDTO toDetailDTO(CarteVisa carte) {
        return CarteVisaDetailResponseDTO.builder()
                .idCarte(carte.getIdCarte())
                .numeroCarteMasque(compteMapper.toCarteVisaResponseDTO(carte).getNumeroCarteMasque())
                .dateExpiration(carte.getDateExpiration())
                .statut(carte.getStatut())
                .plafondJournalier(carte.getPlafondJournalier())
                .numCompte(carte.getCompte() != null ? carte.getCompte().getNumCompte() : null)
                .build();
    }
}
