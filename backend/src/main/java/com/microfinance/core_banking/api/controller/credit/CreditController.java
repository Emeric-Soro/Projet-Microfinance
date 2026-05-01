package com.microfinance.core_banking.api.controller.credit;

import com.microfinance.core_banking.dto.request.credit.DecaissementRequestDTO;
import com.microfinance.core_banking.dto.request.credit.RemboursementRequestDTO;
import com.microfinance.core_banking.dto.response.credit.CreditResponseDTO;
import com.microfinance.core_banking.dto.response.credit.TableauAmortissementResponseDTO;
import com.microfinance.core_banking.entity.Credit;
import com.microfinance.core_banking.entity.Echeance;
import com.microfinance.core_banking.mapper.CreditMapper;
import com.microfinance.core_banking.service.credit.CreditService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/credits")
// Controller REST pour la gestion des credits actifs.
public class CreditController {

	private final CreditService creditService;
	private final CreditMapper creditMapper;

	public CreditController(CreditService creditService, CreditMapper creditMapper) {
		this.creditService = creditService;
		this.creditMapper = creditMapper;
	}

	// Consulte le detail d'un credit.
	@GetMapping("/{idCredit}")
	public ResponseEntity<CreditResponseDTO> consulterCredit(@PathVariable Long idCredit) {
		Credit credit = creditService.consulterCredit(idCredit);
		return ResponseEntity.ok(creditMapper.toCreditResponseDTO(credit));
	}

	// Decaisse un credit approuve (verse les fonds sur le compte du client).
	@PostMapping("/{idCredit}/decaissement")
	public ResponseEntity<CreditResponseDTO> decaisser(
			@PathVariable Long idCredit,
			@Valid @RequestBody DecaissementRequestDTO request) {

		Credit credit = creditService.decaisserCredit(idCredit, request.numCompteCible());
		return ResponseEntity.ok(creditMapper.toCreditResponseDTO(credit));
	}

	// Enregistre un remboursement sur un credit actif.
	@PostMapping("/{idCredit}/remboursement")
	public ResponseEntity<CreditResponseDTO> rembourser(
			@PathVariable Long idCredit,
			@Valid @RequestBody RemboursementRequestDTO request) {

		Credit credit = creditService.enregistrerRemboursement(idCredit, request.montant());
		return ResponseEntity.ok(creditMapper.toCreditResponseDTO(credit));
	}

	// Consulte le tableau d'amortissement d'un credit.
	@GetMapping("/{idCredit}/echeancier")
	public ResponseEntity<TableauAmortissementResponseDTO> consulterEcheancier(
			@PathVariable Long idCredit) {

		Credit credit = creditService.consulterCredit(idCredit);
		List<Echeance> echeances = creditService.consulterTableauAmortissement(idCredit);
		return ResponseEntity.ok(creditMapper.toTableauAmortissementResponseDTO(credit, echeances));
	}

	// Liste les credits d'un client.
	@GetMapping("/client/{idClient}")
	public ResponseEntity<Page<CreditResponseDTO>> creditsClient(
			@PathVariable Long idClient, Pageable pageable) {

		Page<Credit> credits = creditService.consulterCreditsClient(idClient, pageable);
		return ResponseEntity.ok(credits.map(creditMapper::toCreditResponseDTO));
	}
}
