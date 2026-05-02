package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EcheanceCreditResponseDTO {
    private Long idEcheanceCredit;
    private Integer numeroEcheance;
    private LocalDate dateEcheance;
    private BigDecimal capitalPrevu;
    private BigDecimal interetPrevu;
    private BigDecimal assurancePrevue;
    private BigDecimal totalPrevu;
    private BigDecimal capitalPaye;
    private BigDecimal interetPaye;
    private BigDecimal assurancePayee;
    private String statut;
}
