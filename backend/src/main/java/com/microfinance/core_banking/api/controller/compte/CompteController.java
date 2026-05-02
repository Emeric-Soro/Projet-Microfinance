package com.microfinance.core_banking.api.controller.compte;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.compte.ChangementDecouvertRequestDTO;
import com.microfinance.core_banking.dto.request.compte.ChangementStatutCompteRequestDTO;
import com.microfinance.core_banking.dto.request.compte.ClotureCompteRequestDTO;
import com.microfinance.core_banking.dto.request.compte.OuvertureCompteRequestDTO;
import com.microfinance.core_banking.dto.response.extension.ActionEnAttenteResponseDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.dto.response.compte.CompteResponseDTO;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.StatutCompte;
import com.microfinance.core_banking.mapper.CompteMapper;
import com.microfinance.core_banking.service.compte.CompteService;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
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
	private final PendingActionSubmissionService pendingActionSubmissionService;

	public CompteController(CompteService compteService, CompteMapper compteMapper, PendingActionSubmissionService pendingActionSubmissionService) {
		this.compteService = compteService;
		this.compteMapper = compteMapper;
		this.pendingActionSubmissionService = pendingActionSubmissionService;
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
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
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
            summary = "Soumettre une ouverture sensible",
            description = "Soumet une ouverture de compte sensible pour approbation superviseur"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Ouverture soumise en attente de validation"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    @PostMapping("/ouvertures-sensibles")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    @AuditLog(action = "ACCOUNT_OPEN_SENSITIVE_SUBMIT", resource = "COMPTE")
    public ResponseEntity<ActionEnAttenteResponseDTO> soumettreOuvertureSensible(
			@Valid @RequestBody OuvertureCompteRequestDTO requestDTO
	) {
		ActionEnAttente action = pendingActionSubmissionService.submit("OPEN_COMPTE_SENSIBLE", "COMPTE", null, requestDTO, "Ouverture de compte sensible soumise");
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
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
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER) or (hasAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_CLIENT) and @accountAccessSecurity.canAccessAccount(authentication, #numCompte))")
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
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
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
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    @AuditLog(action = "ACCOUNT_CLOSE", resource = "COMPTE")
	public ResponseEntity<CompteResponseDTO> cloturerCompte(@PathVariable String numCompte) {
		// Marque le compte comme ferme via l'historique des statuts.
		Compte compte = compteService.cloturerCompte(numCompte);
		return ResponseEntity.ok(toCompteResponse(compte));
	}

    @Operation(
            summary = "Soumettre une cloture de compte",
            description = "Soumet une demande de cloture de compte pour approbation superviseur"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Cloture soumise en attente de validation"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    @PutMapping("/cloture/soumettre")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    @AuditLog(action = "ACCOUNT_CLOSE_SUBMIT", resource = "COMPTE")
    public ResponseEntity<ActionEnAttenteResponseDTO> soumettreClotureCompte(
			@Valid @RequestBody ClotureCompteRequestDTO requestDTO
	) {
		ActionEnAttente action = pendingActionSubmissionService.submit("CLOTURE_COMPTE", "COMPTE", requestDTO.getNumCompte(), requestDTO, "Cloture de compte soumise");
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
	}

    @Operation(
            summary = "Soumettre un blocage de compte",
            description = "Soumet une demande de blocage de compte pour approbation superviseur"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Blocage soumis en attente de validation"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    @PutMapping("/blocage/soumettre")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    @AuditLog(action = "ACCOUNT_BLOCK_SUBMIT", resource = "COMPTE")
    public ResponseEntity<ActionEnAttenteResponseDTO> soumettreBlocageCompte(
			@Valid @RequestBody ChangementStatutCompteRequestDTO requestDTO
	) {
		ActionEnAttente action = pendingActionSubmissionService.submit("BLOCK_COMPTE", "COMPTE", requestDTO.getNumCompte(), requestDTO, "Blocage de compte soumis");
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
	}

    @Operation(
            summary = "Soumettre un deblocage de compte",
            description = "Soumet une demande de deblocage de compte pour approbation superviseur"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Deblocage soumis en attente de validation"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    @PutMapping("/deblocage/soumettre")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    @AuditLog(action = "ACCOUNT_UNBLOCK_SUBMIT", resource = "COMPTE")
    public ResponseEntity<ActionEnAttenteResponseDTO> soumettreDeblocageCompte(
			@Valid @RequestBody ChangementStatutCompteRequestDTO requestDTO
	) {
		ActionEnAttente action = pendingActionSubmissionService.submit("UNBLOCK_COMPTE", "COMPTE", requestDTO.getNumCompte(), requestDTO, "Deblocage de compte soumis");
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
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

	private ActionEnAttenteResponseDTO toActionDto(ActionEnAttente action) {
		ActionEnAttenteResponseDTO dto = new ActionEnAttenteResponseDTO();
		dto.setIdActionEnAttente(action.getIdActionEnAttente());
		dto.setTypeAction(action.getTypeAction());
		dto.setRessource(action.getRessource());
		dto.setReferenceRessource(action.getReferenceRessource());
		dto.setStatut(action.getStatut());
		return dto;
	}
}
