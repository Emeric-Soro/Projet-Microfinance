package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ImpayeCreditResponseDTO {
    private Long idImpayeCredit;
    private Long idCredit;
    private Long idEcheanceCredit;
    private BigDecimal montant;
    private Integer joursRetard;
    private String classeRisque;
    private String statut;
}
