package com.soutra.microfinance.api.controller.mobile;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.response.mobile.MobileOperationResponseDTO;
import com.soutra.microfinance.dto.response.mobile.MobileRecuResponseDTO;
import com.soutra.microfinance.entity.Transaction;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.service.operation.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/mobile/operations")
@Tag(name = "Mobile Operations", description = "API de consultation des operations pour l'application mobile")
public class MobileOperationController {

    private final TransactionService transactionService;

    public MobileOperationController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Historique des operations", description = "Retourne l'historique pagine des operations du client connecte.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historique retourne avec succes")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_OPERATION_HISTORY", resource = "OPERATION")
    public ResponseEntity<Page<MobileOperationResponseDTO>> historiqueOperations(
            @ParameterObject Pageable pageable,
            Authentication authentication
    ) {
        Utilisateur utilisateur = extraireUtilisateur(authentication);
        Page<Transaction> transactions = transactionService.listerTransactionsUtilisateur(
                utilisateur.getIdUser(), pageable);
        Page<MobileOperationResponseDTO> response = transactions.map(this::toOperationResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Detail d'une operation", description = "Retourne le detail d'une operation par sa reference.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detail retourne avec succes"),
            @ApiResponse(responseCode = "404", description = "Operation introuvable")
    })
    @GetMapping("/{reference}")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_OPERATION_DETAIL", resource = "OPERATION")
    public ResponseEntity<MobileOperationResponseDTO> detailOperation(
            @PathVariable String reference,
            Authentication authentication
    ) {
        Utilisateur utilisateur = extraireUtilisateur(authentication);
        Transaction transaction = transactionService.getDetailTransaction(reference);

        verifierProprietaireTransaction(transaction, utilisateur);

        return ResponseEntity.ok(toOperationResponse(transaction));
    }

    @Operation(summary = "Recu d'une operation", description = "Retourne le recu detaille d'une operation par sa reference.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recu retourne avec succes"),
            @ApiResponse(responseCode = "404", description = "Operation introuvable")
    })
    @GetMapping("/{reference}/recu")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_OPERATION_RECEIPT", resource = "OPERATION")
    public ResponseEntity<MobileRecuResponseDTO> recuOperation(
            @PathVariable String reference,
            Authentication authentication
    ) {
        Utilisateur utilisateur = extraireUtilisateur(authentication);
        Transaction transaction = transactionService.getDetailTransaction(reference);

        verifierProprietaireTransaction(transaction, utilisateur);

        MobileRecuResponseDTO response = new MobileRecuResponseDTO(
                transaction.getReferenceUnique(),
                transaction.getTypeTransaction() != null ? transaction.getTypeTransaction().getLibelle() : null,
                transaction.getMontantGlobal(),
                transaction.getFrais() != null ? transaction.getFrais() : BigDecimal.ZERO,
                transaction.getCompteSource() != null ? transaction.getCompteSource().getNumCompte() : null,
                transaction.getDateHeureTransaction(),
                transaction.getStatutOperation() != null ? transaction.getStatutOperation().name() : null
        );

        return ResponseEntity.ok(response);
    }

    private MobileOperationResponseDTO toOperationResponse(Transaction transaction) {
        return new MobileOperationResponseDTO(
                transaction.getReferenceUnique(),
                transaction.getTypeTransaction() != null ? transaction.getTypeTransaction().getLibelle() : null,
                transaction.getMontantGlobal(),
                transaction.getDateHeureTransaction(),
                transaction.getStatutOperation() != null ? transaction.getStatutOperation().name() : null
        );
    }

    private void verifierProprietaireTransaction(Transaction transaction, Utilisateur utilisateur) {
        if (!transaction.getUtilisateur().getIdUser().equals(utilisateur.getIdUser())) {
            throw new IllegalArgumentException("Operation introuvable");
        }
    }

    private Utilisateur extraireUtilisateur(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof Utilisateur utilisateur)) {
            throw new IllegalStateException("Utilisateur authentifie introuvable");
        }
        return utilisateur;
    }
}
