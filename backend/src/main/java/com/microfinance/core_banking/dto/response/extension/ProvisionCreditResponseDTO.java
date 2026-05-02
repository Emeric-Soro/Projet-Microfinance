package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProvisionCreditResponseDTO {
    private Long idProvisionCredit;
    private Long idCredit;
    private LocalDate dateCalcul;
    private BigDecimal tauxProvision;
    private BigDecimal montantProvision;
    private String referencePieceComptable;
    private String statut;
}
