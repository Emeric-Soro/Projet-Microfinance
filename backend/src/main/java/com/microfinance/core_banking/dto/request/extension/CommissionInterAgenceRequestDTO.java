package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CommissionInterAgenceRequestDTO {
    private BigDecimal tauxCommission;
    private BigDecimal montantCommission;
    private Long idCompteComptable;
    private String statutCommission;
    private String referencePiece;
    private LocalDateTime dateComptabilisation;
}
