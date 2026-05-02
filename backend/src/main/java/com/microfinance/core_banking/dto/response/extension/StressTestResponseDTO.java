package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StressTestResponseDTO {
    private Long idStressTest;
    private String codeScenario;
    private String libelle;
    private BigDecimal tauxDefaut;
    private BigDecimal tauxRetrait;
    private String statut;
}
