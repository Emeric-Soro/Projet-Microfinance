package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResultatStressTestResponseDTO {
    private Long idResultatStressTest;
    private String stressTest;
    private BigDecimal encoursCredit;
    private BigDecimal pertesProjetees;
    private BigDecimal retraitsProjetes;
    private BigDecimal liquiditeNette;
    private String statutResultat;
}
