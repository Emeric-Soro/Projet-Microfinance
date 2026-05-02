package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "Ligne de balance comptable par compte")
public class BalanceLineDTO {
    @Schema(description = "Numéro de compte comptable", example = "512000")
    private String numeroCompte;

    @Schema(description = "Intitulé du compte", example = "Banque")
    private String intitule;

    @Schema(description = "Total des montants au débit en XOF", example = "5000000.00")
    private BigDecimal debit;

    @Schema(description = "Total des montants au crédit en XOF", example = "3000000.00")
    private BigDecimal credit;

    @Schema(description = "Solde net du compte en XOF", example = "2000000.00")
    private BigDecimal solde;
}
