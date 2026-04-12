package com.microfinance.core_banking.api.controller.operation;

import com.microfinance.core_banking.dto.request.operation.TransactionSimpleRequestDTO;
import com.microfinance.core_banking.dto.request.operation.VirementRequestDTO;
import com.microfinance.core_banking.dto.response.operation.LigneReleveResponseDTO;
import com.microfinance.core_banking.dto.response.operation.RecuTransactionResponseDTO;
import com.microfinance.core_banking.entity.LigneEcriture;
import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.mapper.OperationMapper;
import com.microfinance.core_banking.service.operation.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "API des operations bancaires")
public class TransactionController {

	private final TransactionService transactionService;
	private final OperationMapper operationMapper;

	public TransactionController(TransactionService transactionService, OperationMapper operationMapper) {
		this.transactionService = transactionService;
		this.operationMapper = operationMapper;
	}

	@Operation(
			summary = "Effectuer un depot",
			description = "Credite un compte et retourne le recu de transaction"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Depot enregistre avec succes"),
			@ApiResponse(responseCode = "400", description = "Donnees invalides"),
			@ApiResponse(responseCode = "404", description = "Compte ou utilisateur introuvable"),
			@ApiResponse(responseCode = "409", description = "Conflit metier")
	})
	@PostMapping("/depot")
	public ResponseEntity<RecuTransactionResponseDTO> faireDepot(
			@Valid @RequestBody TransactionSimpleRequestDTO requestDTO
	) {
		// Cree une transaction de depot sur le compte cible.
		Transaction transaction = transactionService.faireDepot(
				requestDTO.getNumCompte(),
				requestDTO.getMontant(),
				requestDTO.getIdGuichetier()
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(operationMapper.toRecuResponseDTO(transaction));
	}

	@Operation(
			summary = "Effectuer un retrait",
			description = "Debite un compte et retourne le recu de transaction"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Retrait enregistre avec succes"),
			@ApiResponse(responseCode = "400", description = "Donnees invalides"),
			@ApiResponse(responseCode = "404", description = "Compte ou utilisateur introuvable"),
			@ApiResponse(responseCode = "409", description = "Fonds insuffisants ou conflit metier")
	})
	@PostMapping("/retrait")
	public ResponseEntity<RecuTransactionResponseDTO> faireRetrait(
			@Valid @RequestBody TransactionSimpleRequestDTO requestDTO
	) {
		// Cree une transaction de retrait avec controle des fonds disponibles.
		Transaction transaction = transactionService.faireRetrait(
				requestDTO.getNumCompte(),
				requestDTO.getMontant(),
				requestDTO.getIdGuichetier()
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(operationMapper.toRecuResponseDTO(transaction));
	}

	@Operation(
			summary = "Effectuer un virement",
			description = "Debite le compte source, credite le compte destination et retourne le recu"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Virement enregistre avec succes"),
			@ApiResponse(responseCode = "400", description = "Donnees invalides"),
			@ApiResponse(responseCode = "404", description = "Compte ou utilisateur introuvable"),
			@ApiResponse(responseCode = "409", description = "Fonds insuffisants ou conflit metier")
	})
	@PostMapping("/virement")
	public ResponseEntity<RecuTransactionResponseDTO> faireVirement(
			@Valid @RequestBody VirementRequestDTO requestDTO,
			@RequestParam Long idGuichetier
	) {
		// Cree une transaction de virement entre deux comptes distincts.
		Transaction transaction = transactionService.faireVirement(
				requestDTO.getCompteSource(),
				requestDTO.getCompteDestination(),
				requestDTO.getMontant(),
				idGuichetier
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(operationMapper.toRecuResponseDTO(transaction));
	}

	@Operation(
			summary = "Consulter l'historique d'un compte",
			description = "Retourne les lignes d'ecriture paginees associees a un compte"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Historique retourne avec succes"),
			@ApiResponse(responseCode = "400", description = "Parametres invalides"),
			@ApiResponse(responseCode = "404", description = "Compte introuvable")
	})
	@GetMapping("/comptes/{numCompte}/historique")
	public ResponseEntity<Page<LigneReleveResponseDTO>> consulterHistorique(
			@PathVariable String numCompte,
			@ParameterObject Pageable pageable
	) {
		// Recupere les lignes debit/credit d'un compte dans l'ordre de pagination demande.
		Page<LigneEcriture> pageLignes = transactionService.historiqueOperations(numCompte, pageable);
		Page<LigneReleveResponseDTO> pageReleve = pageLignes.map(operationMapper::toLigneReleveResponseDTO);
		return ResponseEntity.ok(pageReleve);
	}
}
