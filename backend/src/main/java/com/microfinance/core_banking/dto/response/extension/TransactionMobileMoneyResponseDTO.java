package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Transaction Mobile Money effectuée via un wallet")
public class TransactionMobileMoneyResponseDTO {
    @Schema(description = "Identifiant unique de la transaction Mobile Money", example = "1")
    private Long idTransactionMobileMoney;

    @Schema(description = "Référence de la transaction", example = "MM-20260401-0001")
    private String referenceTransaction;

    @Schema(description = "Wallet utilisé", example = "771234567")
    private String wallet;

    @Schema(description = "Type de transaction", example = "DEPOT")
    private String typeTransaction;

    @Schema(description = "Montant de la transaction en XOF", example = "25000.00")
    private BigDecimal montant;

    @Schema(description = "Frais appliqués en XOF", example = "100.00")
    private BigDecimal frais;

    @Schema(description = "Référence de la transaction interne associée", example = "TXN-20260401-000001")
    private String referenceTransactionInterne;

    @Schema(description = "Statut de la transaction", example = "COMPLETED")
    private String statut;
}
