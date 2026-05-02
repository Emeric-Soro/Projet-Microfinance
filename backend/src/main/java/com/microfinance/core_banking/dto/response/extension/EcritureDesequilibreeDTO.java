package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EcritureDesequilibreeDTO {
    private Long idEcritureComptable;
    private String referencePiece;
    private LocalDate dateComptable;
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal ecart;
}
