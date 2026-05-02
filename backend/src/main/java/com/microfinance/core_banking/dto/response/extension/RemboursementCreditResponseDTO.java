package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RemboursementCreditResponseDTO {
    private Long idRemboursementCredit;
    private String referenceRemboursement;
    private BigDecimal montant;
    private BigDecimal capitalPaye;
    private BigDecimal interetPaye;
    private BigDecimal assurancePayee;
    private String referenceTransaction;
    private LocalDateTime datePaiement;
    private String statut;
}
