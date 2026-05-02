package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionMobileMoneyResponseDTO {
    private Long idTransactionMobileMoney;
    private String referenceTransaction;
    private String wallet;
    private String typeTransaction;
    private BigDecimal montant;
    private BigDecimal frais;
    private String referenceTransactionInterne;
    private String statut;
}
