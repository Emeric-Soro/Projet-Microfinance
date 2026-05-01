package com.microfinance.core_banking.api.controller.credit;

import com.microfinance.core_banking.dto.request.credit.DemandeCreditRequestDTO;
import com.microfinance.core_banking.dto.request.credit.DecisionCreditRequestDTO;
import com.microfinance.core_banking.dto.response.credit.DemandeCreditResponseDTO;
import com.microfinance.core_banking.entity.Credit;
import com.microfinance.core_banking.entity.DemandeCredit;
import com.microfinance.core_banking.mapper.CreditMapper;
import com.microfinance.core_banking.service.credit.CreditService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/credits/demandes")
// Controller REST pour la gestion des demandes de credit.
public class DemandeCreditController {

	private final CreditService creditService;
	private final CreditMapper creditMapper;

	public DemandeCreditController(CreditService creditService, CreditMapper creditMapper) {
		this.creditService = creditService;
		this.creditMapper = creditMapper;
	}

	// Soumet une nouvelle demande de credit.
	@PostMapping
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

	// Liste les demandes en attente de decision (pour l'agent de credit).
	@GetMapping
	public ResponseEntity<Page<DemandeCreditResponseDTO>> listerDemandes(Pageable pageable) {
		Page<DemandeCredit> demandes = creditService.listerDemandesEnAttente(pageable);
		return ResponseEntity.ok(demandes.map(creditMapper::toDemandeCreditResponseDTO));
	}

	// Consulte le detail d'une demande.
	@GetMapping("/{idDemande}")
	public ResponseEntity<DemandeCreditResponseDTO> consulterDemande(@PathVariable Long idDemande) {
		DemandeCredit demande = creditService.consulterDemande(idDemande);
		return ResponseEntity.ok(creditMapper.toDemandeCreditResponseDTO(demande));
	}

	// Approuve ou rejette une demande de credit.
	@PutMapping("/{idDemande}/decision")
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
