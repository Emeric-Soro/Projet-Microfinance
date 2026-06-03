package com.soutra.microfinance.api.controller.credit;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.credit.DemandeCreditRequestDTO;
import com.soutra.microfinance.dto.request.credit.DecisionCreditRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.soutra.microfinance.dto.response.credit.DemandeCreditResponseDTO;
import com.soutra.microfinance.dto.response.credit.DecisionCreditResponseDTO;
import com.soutra.microfinance.entity.Credit;
import com.soutra.microfinance.entity.DemandeCredit;
import com.soutra.microfinance.mapper.CreditMapper;
import com.soutra.microfinance.service.credit.CreditService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/credits/demandes")
@Tag(name = "Demandes de Credit", description = "API de gestion des demandes de credit")
public class DemandeCreditController {

	private final CreditService creditService;
	private final CreditMapper creditMapper;

	public DemandeCreditController(CreditService creditService, CreditMapper creditMapper) {
		this.creditService = creditService;
		this.creditMapper = creditMapper;
	}

	@Operation(summary = "Soumettre une demande de credit", description = "Cree une nouvelle demande de credit pour un client")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Demande creee"),
			@ApiResponse(responseCode = "400", description = "Donnees invalides")
	})
	@PostMapping
	@PreAuthorize("hasAnyAuthority('ADMIN','CHEF_AGENCE','GUICHETIER')")
	@AuditLog(action = "CREDIT_DEMAND_SUBMIT", resource = "CREDIT")
	public ResponseEntity<DemandeCreditResponseDTO> soumettreDemande(
			@Valid @RequestBody DemandeCreditRequestDTO request) {

		DemandeCredit demande = creditService.soumettreDemandeCredit(
				request.idClient(),
				request.codeProduitCredit(),
				request.montantDemande(),
				request.dureeSouhaitee(),
				request.objetCredit(),
				request.idAgentCredit()
		);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(creditMapper.toDemandeCreditResponseDTO(demande));
	}

	@Operation(summary = "Lister les demandes en attente", description = "Liste paginee des demandes de credit en attente de decision")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Liste des demandes") })
	@GetMapping
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE')")
	public ResponseEntity<Page<DemandeCreditResponseDTO>> listerDemandes(@ParameterObject Pageable pageable) {
		Page<DemandeCredit> demandes = creditService.listerDemandesEnAttente(pageable);
		return ResponseEntity.ok(demandes.map(creditMapper::toDemandeCreditResponseDTO));
	}

	@Operation(summary = "Consulter une demande", description = "Retourne les details d'une demande de credit")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Details de la demande"),
			@ApiResponse(responseCode = "404", description = "Demande introuvable")
	})
	@GetMapping("/{idDemande}")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE','GUICHETIER')")
	public ResponseEntity<DemandeCreditResponseDTO> consulterDemande(@PathVariable Long idDemande) {
		DemandeCredit demande = creditService.consulterDemande(idDemande);
		return ResponseEntity.ok(creditMapper.toDemandeCreditResponseDTO(demande));
	}

	@Operation(summary = "Decider sur une demande", description = "Approuve ou rejette une demande de credit")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Decision appliquee"),
			@ApiResponse(responseCode = "400", description = "Decision invalide")
	})
	@PutMapping("/{idDemande}/decision")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE')")
	@AuditLog(action = "CREDIT_DEMAND_DECISION", resource = "CREDIT")
	public ResponseEntity<DecisionCreditResponseDTO> decider(@PathVariable Long idDemande,
										  @Valid @RequestBody DecisionCreditRequestDTO request) {

		if ("APPROUVEE".equalsIgnoreCase(request.decision())) {
			Credit credit = creditService.approuverDemande(idDemande);
			return ResponseEntity.ok(new DecisionCreditResponseDTO(
					"APPROUVEE",
					creditMapper.toCreditResponseDTO(credit),
					null
			));
		} else if ("REJETEE".equalsIgnoreCase(request.decision())) {
			DemandeCredit demande = creditService.rejeterDemande(idDemande, request.motifRejet());
			return ResponseEntity.ok(new DecisionCreditResponseDTO(
					"REJETEE",
					null,
					creditMapper.toDemandeCreditResponseDTO(demande)
			));
		} else {
			throw new IllegalArgumentException("Decision invalide. Valeurs acceptees: APPROUVEE, REJETEE");
		}
	}
}
