package com.microfinance.core_banking.api.controller.compte;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.compte.ChangementDecouvertRequestDTO;
import com.microfinance.core_banking.dto.request.compte.OuvertureCompteRequestDTO;
import com.microfinance.core_banking.dto.response.compte.CompteResponseDTO;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.StatutCompte;
import com.microfinance.core_banking.mapper.CompteMapper;
import com.microfinance.core_banking.service.compte.CompteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Comparator;

@RestController
@RequestMapping("/api/comptes")
@Tag(name = "Comptes", description = "API de gestion des comptes bancaires")
public class CompteController {

	private final CompteService compteService;
	private final CompteMapper compteMapper;

	public CompteController(CompteService compteService, CompteMapper compteMapper) {
		this.compteService = compteService;
		this.compteMapper = compteMapper;
	}

	@Operation(
			summary = "Ouvrir un compte",
			description = "Ouvre un nouveau compte pour un client KYC valide avec depot initial"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Compte ouvert avec succes"),
			@ApiResponse(responseCode = "400", description = "Donnees invalides"),
			@ApiResponse(responseCode = "404", description = "Client introuvable"),
			@ApiResponse(responseCode = "409", description = "Conflit metier")
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "ACCOUNT_OPEN", resource = "COMPTE")
	public ResponseEntity<CompteResponseDTO> ouvrirCompte(
			@Valid @RequestBody OuvertureCompteRequestDTO requestDTO
	) {
		// Cree un compte et retourne ses informations principales.
		Compte compte = compteService.ouvrirCompte(
				requestDTO.getIdClient(),
				requestDTO.getCodeTypeCompte(),
				requestDTO.getDepotInitial()
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(toCompteResponse(compte));
	}

	@Operation(
			summary = "Consulter le solde",
			description = "Retourne le solde actuel d'un compte"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Solde retourne avec succes"),
			@ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    @GetMapping("/{numCompte}/solde")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER') or (hasAuthority('CLIENT') and @accountAccessSecurity.canAccessAccount(authentication, #numCompte))")
    public ResponseEntity<BigDecimal> consulterSolde(@PathVariable String numCompte, Authentication authentication) {
		// Lit le solde courant sans modifier l'etat du compte.
		BigDecimal solde = compteService.consulterSolde(numCompte);
		return ResponseEntity.ok(solde);
	}

	@Operation(
			summary = "Modifier le decouvert autorise",
			description = "Met a jour le plafond de decouvert d'un compte"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Decouvert mis a jour avec succes"),
			@ApiResponse(responseCode = "400", description = "Donnees invalides"),
			@ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    @PutMapping("/decouvert")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "ACCOUNT_OVERDRAFT_UPDATE", resource = "COMPTE")
    public ResponseEntity<CompteResponseDTO> changerDecouvertAutorise(
			@Valid @RequestBody ChangementDecouvertRequestDTO requestDTO
	) {
		// Applique un nouveau plafond de decouvert sur le compte cible.
		Compte compte = compteService.changerDecouvertAutorise(
				requestDTO.getNumCompte(),
				requestDTO.getNouveauPlafond()
		);
		return ResponseEntity.ok(toCompteResponse(compte));
	}

	@Operation(
			summary = "Cloturer un compte",
			description = "Cloture un compte si les conditions metier sont respectees"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Compte cloture avec succes"),
			@ApiResponse(responseCode = "404", description = "Compte introuvable"),
			@ApiResponse(responseCode = "409", description = "Compte non cloturable")
    })
    @PutMapping("/{numCompte}/cloture")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "ACCOUNT_CLOSE", resource = "COMPTE")
    public ResponseEntity<CompteResponseDTO> cloturerCompte(@PathVariable String numCompte) {
		// Marque le compte comme ferme via l'historique des statuts.
		Compte compte = compteService.cloturerCompte(numCompte);
		return ResponseEntity.ok(toCompteResponse(compte));
	}

	private CompteResponseDTO toCompteResponse(Compte compte) {
		CompteResponseDTO responseDTO = compteMapper.toCompteResponseDTO(compte);
		responseDTO.setStatut(extraireStatutCourant(compte));
		return responseDTO;
	}

	private String extraireStatutCourant(Compte compte) {
		if (compte.getStatutsCompte() == null || compte.getStatutsCompte().isEmpty()) {
			return null;
		}

		return compte.getStatutsCompte().stream()
				.max(Comparator.comparing(StatutCompte::getDateStatut, Comparator.nullsLast(Comparator.naturalOrder())))
				.map(StatutCompte::getLibelleStatut)
				.orElse(null);
	}
}
