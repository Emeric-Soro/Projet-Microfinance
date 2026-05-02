package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReseauReportingDTO {
    private Long idAgence;
    private String codeAgence;
    private String nomAgence;
    private Long clients;
    private Long comptes;
    private Long credits;
    private BigDecimal volumeTransactions;
}
