package com.soutra.microfinance.api.controller.credit;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.credit.DecaissementRequestDTO;
import com.soutra.microfinance.dto.request.credit.RemboursementRequestDTO;
import com.soutra.microfinance.dto.request.credit.SimulationRequestDTO;
import com.soutra.microfinance.dto.response.credit.CreditResponseDTO;
import com.soutra.microfinance.dto.response.credit.TableauAmortissementResponseDTO;
import com.soutra.microfinance.entity.Credit;
import com.soutra.microfinance.entity.Echeance;
import com.soutra.microfinance.entity.MethodeCalculInteret;
import com.soutra.microfinance.mapper.CreditMapper;
import com.soutra.microfinance.service.credit.AmortissementService;
import com.soutra.microfinance.service.credit.CreditService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/credits")
public class CreditController {

	private final CreditService creditService;
	private final CreditMapper creditMapper;
	private final AmortissementService amortissementService;

	public CreditController(CreditService creditService, CreditMapper creditMapper, AmortissementService amortissementService) {
		this.creditService = creditService;
		this.creditMapper = creditMapper;
		this.amortissementService = amortissementService;
	}

	@GetMapping("/{idCredit}")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE','GUICHETIER')")
	public ResponseEntity<CreditResponseDTO> consulterCredit(@PathVariable Long idCredit) {
		Credit credit = creditService.consulterCredit(idCredit);
		return ResponseEntity.ok(creditMapper.toCreditResponseDTO(credit));
	}

	@PostMapping("/{idCredit}/decaissement")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE')")
	@AuditLog(action = "CREDIT_DISBURSEMENT", resource = "CREDIT")
	public ResponseEntity<CreditResponseDTO> decaisser(
			@PathVariable Long idCredit,
			@Valid @RequestBody DecaissementRequestDTO request) {

		Credit credit = creditService.decaisserCredit(idCredit, request.numCompteCible());
		return ResponseEntity.ok(creditMapper.toCreditResponseDTO(credit));
	}

	@PostMapping("/{idCredit}/remboursement")
	@PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
	@AuditLog(action = "CREDIT_REPAYMENT", resource = "CREDIT")
	public ResponseEntity<CreditResponseDTO> rembourser(
			@PathVariable Long idCredit,
			@Valid @RequestBody RemboursementRequestDTO request) {

		Credit credit = creditService.enregistrerRemboursement(idCredit, request.montant());
		return ResponseEntity.ok(creditMapper.toCreditResponseDTO(credit));
	}

	@GetMapping("/{idCredit}/echeancier")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE','GUICHETIER')")
	public ResponseEntity<TableauAmortissementResponseDTO> consulterEcheancier(
			@PathVariable Long idCredit) {

		Credit credit = creditService.consulterCredit(idCredit);
		List<Echeance> echeances = creditService.consulterTableauAmortissement(idCredit);
		return ResponseEntity.ok(creditMapper.toTableauAmortissementResponseDTO(credit, echeances));
	}

	@GetMapping("/client/{idClient}")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE','GUICHETIER')")
	public ResponseEntity<Page<CreditResponseDTO>> creditsClient(
			@PathVariable Long idClient, Pageable pageable) {

		Page<Credit> credits = creditService.consulterCreditsClient(idClient, pageable);
		return ResponseEntity.ok(credits.map(creditMapper::toCreditResponseDTO));
	}

	@PostMapping("/simulation")
	@PreAuthorize("permitAll()")
	public ResponseEntity<List<Echeance>> simulerCredit(
			@Valid @RequestBody SimulationRequestDTO request) {

		MethodeCalculInteret methode = MethodeCalculInteret.valueOf(request.methode().toUpperCase());
		List<Echeance> echeances = amortissementService.genererTableau(
				request.montant(),
				request.taux(),
				request.duree(),
				methode,
				LocalDate.now()
		);
		return ResponseEntity.ok(echeances);
	}
}
