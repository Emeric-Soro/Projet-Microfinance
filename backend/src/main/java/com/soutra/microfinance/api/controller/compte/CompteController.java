package com.soutra.microfinance.api.controller.compte;

import com.soutra.microfinance.api.helper.SoutraSecurityHelper;
import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.client.ReleveRequestDTO;
import com.soutra.microfinance.dto.request.compte.ChangementDecouvertRequestDTO;
import com.soutra.microfinance.dto.request.compte.ChangementStatutCompteRequestDTO;
import com.soutra.microfinance.dto.request.compte.ClotureCompteRequestDTO;
import com.soutra.microfinance.dto.request.compte.OuvertureCompteRequestDTO;
import com.soutra.microfinance.dto.request.operation.DepotInitialRequestDTO;
import com.soutra.microfinance.dto.response.compte.CompteResponseDTO;
import com.soutra.microfinance.dto.response.operation.RecuTransactionResponseDTO;
import com.soutra.microfinance.entity.Compte;
import com.soutra.microfinance.entity.ReleveFormat;
import com.soutra.microfinance.entity.StatutCompte;
import com.soutra.microfinance.entity.StatutOperation;
import com.soutra.microfinance.entity.Transaction;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.mapper.CompteMapper;
import com.soutra.microfinance.mapper.OperationMapper;
import com.soutra.microfinance.service.compte.CompteService;
import com.soutra.microfinance.service.compte.ReleveService;
import com.soutra.microfinance.service.operation.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/v1/comptes")
@Tag(name = "Comptes", description = "API de gestion des comptes bancaires")
public class CompteController {

	private final CompteService compteService;
	private final CompteMapper compteMapper;
	private final ReleveService releveService;
	private final TransactionService transactionService;
	private final OperationMapper operationMapper;

	public CompteController(CompteService compteService, CompteMapper compteMapper, ReleveService releveService,
			TransactionService transactionService, OperationMapper operationMapper) {
		this.compteService = compteService;
		this.compteMapper = compteMapper;
		this.releveService = releveService;
		this.transactionService = transactionService;
		this.operationMapper = operationMapper;
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
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR')")
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
			summary = "Obtenir les details d'un compte",
			description = "Retourne les details complets d'un compte"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Details retournes avec succes"),
			@ApiResponse(responseCode = "404", description = "Compte introuvable")
	})
	@GetMapping("/{numCompte}")
	@PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR')")
	public ResponseEntity<CompteResponseDTO> obtenirDetailsCompte(@PathVariable String numCompte) {
		Compte compte = compteService.obtenirCompteParNumero(numCompte);
		return ResponseEntity.ok(toCompteResponse(compte));
	}

	@Operation(
			summary = "Lister les comptes d'un client",
			description = "Retourne tous les comptes d'un client"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Comptes retournes avec succes"),
			@ApiResponse(responseCode = "404", description = "Client introuvable")
	})
	@GetMapping("/client/{idClient}")
	@PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR')")
	public ResponseEntity<Page<CompteResponseDTO>> listerComptesClient(
			@PathVariable Long idClient,
			@ParameterObject Pageable pageable
	) {
		Page<Compte> comptes = compteService.listerComptesClient(idClient, pageable);
		return ResponseEntity.ok(comptes.map(this::toCompteResponse));
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
    public ResponseEntity<CompteResponseDTO> cloturerCompte(
            @PathVariable String numCompte,
            @Valid @RequestBody(required = false) ClotureCompteRequestDTO requestDTO
    ) {
        String motif = requestDTO != null ? requestDTO.getMotif() : null;
        Compte compte = compteService.cloturerCompte(numCompte);
        return ResponseEntity.ok(toCompteResponse(compte));
    }

    @Operation(
            summary = "Bloquer un compte",
            description = "Bloque un compte actif avec un motif"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compte bloque avec succes"),
            @ApiResponse(responseCode = "404", description = "Compte introuvable"),
            @ApiResponse(responseCode = "409", description = "Compte deja bloque ou cloture")
    })
    @PutMapping("/{numCompte}/blocage")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "ACCOUNT_BLOCK", resource = "COMPTE")
    public ResponseEntity<CompteResponseDTO> bloquerCompte(
            @PathVariable String numCompte,
            @Valid @RequestBody(required = false) ChangementStatutCompteRequestDTO requestDTO
    ) {
        String motif = requestDTO != null ? requestDTO.getMotif() : null;
        Compte compte = compteService.bloquerCompte(numCompte, motif);
        return ResponseEntity.ok(toCompteResponse(compte));
    }

    @Operation(
            summary = "Debloquer un compte",
            description = "Debloque un compte bloque avec un motif"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compte debloque avec succes"),
            @ApiResponse(responseCode = "404", description = "Compte introuvable"),
            @ApiResponse(responseCode = "409", description = "Le compte n'est pas bloque")
    })
    @PutMapping("/{numCompte}/deblocage")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "ACCOUNT_UNBLOCK", resource = "COMPTE")
    public ResponseEntity<CompteResponseDTO> debloquerCompte(
            @PathVariable String numCompte,
            @Valid @RequestBody(required = false) ChangementStatutCompteRequestDTO requestDTO
    ) {
        String motif = requestDTO != null ? requestDTO.getMotif() : null;
        Compte compte = compteService.debloquerCompte(numCompte, motif);
        return ResponseEntity.ok(toCompteResponse(compte));
    }

    @Operation(
            summary = "Generer un releve de compte",
            description = "Genere un releve de compte au format PDF ou CSV. " +
                    "Plafond : 90 jours pour PDF, 365 jours pour CSV."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Releve genere avec succes"),
            @ApiResponse(responseCode = "400", description = "Parametres invalides ou plage depasse le plafond"),
            @ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    @GetMapping("/{numCompte}/releve")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR')")
    @AuditLog(action = "ACCOUNT_STATEMENT", resource = "COMPTE")
    public ResponseEntity<byte[]> genererReleve(
            @PathVariable String numCompte,
            @Valid ReleveRequestDTO requestDTO
    ) {
        byte[] content = releveService.genererReleve(
                numCompte,
                requestDTO.getDu(),
                requestDTO.getAu(),
                requestDTO.getFormat()
        );

        String filename = "releve_" + numCompte + "_" + requestDTO.getDu() + "_" + requestDTO.getAu();
        MediaType mediaType;
        if (requestDTO.getFormat() == ReleveFormat.PDF) {
            mediaType = MediaType.APPLICATION_PDF;
            filename += ".pdf";
        } else {
            mediaType = MediaType.parseMediaType("text/csv");
            filename += ".csv";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .body(content);
    }

	@Operation(
			summary = "Depot initial sur un compte",
			description = "Effectue le premier depot sur un compte nouvellement ouvert. "
					+ "Ne necessite pas de caisse ouverte : accessible a l'agent commercial et au guichetier."
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Depot initial execute avec succes"),
			@ApiResponse(responseCode = "202", description = "Depot en attente de validation superviseur"),
			@ApiResponse(responseCode = "400", description = "Donnees invalides"),
			@ApiResponse(responseCode = "404", description = "Compte ou utilisateur introuvable"),
			@ApiResponse(responseCode = "409", description = "Conflit metier")
	})
	@PostMapping("/{numCompte}/depot-initial")
	@PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR','AGENT_COMMERCIAL')")
	@AuditLog(action = "ACCOUNT_INITIAL_DEPOSIT", resource = "COMPTE")
	public ResponseEntity<RecuTransactionResponseDTO> depotInitial(
			@PathVariable String numCompte,
			@Valid @RequestBody DepotInitialRequestDTO requestDTO
	) {
		Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
		Transaction transaction = transactionService.faireDepotInitial(
				numCompte,
				requestDTO.getMontant(),
				utilisateur.getIdUser()
		);
		org.springframework.http.HttpStatus status = transaction.getStatutOperation() == StatutOperation.EN_ATTENTE
				? org.springframework.http.HttpStatus.ACCEPTED
				: org.springframework.http.HttpStatus.CREATED;
		return ResponseEntity.status(status).body(operationMapper.toRecuResponseDTO(transaction));
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
