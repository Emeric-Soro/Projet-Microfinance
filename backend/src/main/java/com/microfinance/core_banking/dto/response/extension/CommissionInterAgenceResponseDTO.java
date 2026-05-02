package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CommissionInterAgenceResponseDTO {
    private Long idCommissionInterAgence;
    private Long idOperationDeplacee;
    private BigDecimal tauxCommission;
    private BigDecimal montantCommission;
    private Long idCompteComptable;
    private String statut;
    private LocalDate dateCalcul;
    private LocalDateTime dateComptabilisation;
}
