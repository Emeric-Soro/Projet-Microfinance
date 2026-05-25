package com.soutra.microfinance.api.controller.credit;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.credit.DemandeCreditRequestDTO;
import com.soutra.microfinance.dto.request.credit.DecisionCreditRequestDTO;
import com.soutra.microfinance.dto.response.credit.DemandeCreditResponseDTO;
import com.soutra.microfinance.entity.Credit;
import com.soutra.microfinance.entity.DemandeCredit;
import com.soutra.microfinance.mapper.CreditMapper;
import com.soutra.microfinance.service.credit.CreditService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/credits/demandes")
public class DemandeCreditController {

	private final CreditService creditService;
	private final CreditMapper creditMapper;

	public DemandeCreditController(CreditService creditService, CreditMapper creditMapper) {
		this.creditService = creditService;
		this.creditMapper = creditMapper;
	}

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

	@GetMapping
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE')")
	public ResponseEntity<Page<DemandeCreditResponseDTO>> listerDemandes(Pageable pageable) {
		Page<DemandeCredit> demandes = creditService.listerDemandesEnAttente(pageable);
		return ResponseEntity.ok(demandes.map(creditMapper::toDemandeCreditResponseDTO));
	}

	@GetMapping("/{idDemande}")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE','GUICHETIER')")
	public ResponseEntity<DemandeCreditResponseDTO> consulterDemande(@PathVariable Long idDemande) {
		DemandeCredit demande = creditService.consulterDemande(idDemande);
		return ResponseEntity.ok(creditMapper.toDemandeCreditResponseDTO(demande));
	}

	@PutMapping("/{idDemande}/decision")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE')")
	@AuditLog(action = "CREDIT_DEMAND_DECISION", resource = "CREDIT")
	public ResponseEntity<?> decider(@PathVariable Long idDemande,
									  @Valid @RequestBody DecisionCreditRequestDTO request) {

		if ("APPROUVEE".equalsIgnoreCase(request.decision())) {
			Credit credit = creditService.approuverDemande(idDemande);
			return ResponseEntity.ok(creditMapper.toCreditResponseDTO(credit));
		} else if ("REJETEE".equalsIgnoreCase(request.decision())) {
			DemandeCredit demande = creditService.rejeterDemande(idDemande, request.motifRejet());
			return ResponseEntity.ok(creditMapper.toDemandeCreditResponseDTO(demande));
		} else {
			return ResponseEntity.badRequest().body("Decision invalide. Valeurs acceptees: APPROUVEE, REJETEE");
		}
	}
}
