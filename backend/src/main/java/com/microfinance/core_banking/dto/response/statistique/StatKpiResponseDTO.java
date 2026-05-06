package com.microfinance.core_banking.dto.response.statistique;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class StatKpiResponseDTO {

    private long totalClientsActifs;
    private BigDecimal totalDepots;
    private BigDecimal totalCreditsEnCours;
    private BigDecimal tauxCreditsEnRetard;
}
