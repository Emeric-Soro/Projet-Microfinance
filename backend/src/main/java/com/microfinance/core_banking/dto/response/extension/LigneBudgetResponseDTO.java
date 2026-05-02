package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LigneBudgetResponseDTO {
    private Long idLigneBudget;
    private String rubrique;
    private BigDecimal montantPrevu;
    private BigDecimal montantEngage;
    private BigDecimal montantConsomme;
}
