package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BalanceLineDTO {
    private String numeroCompte;
    private String intitule;
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal solde;
}
