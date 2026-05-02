package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Ligne de budget détaillée par rubrique")
public class LigneBudgetResponseDTO {
    @Schema(description = "Identifiant unique de la ligne budgétaire", example = "1")
    private Long idLigneBudget;

    @Schema(description = "Rubrique budgétaire", example = "Fournitures de bureau")
    private String rubrique;

    @Schema(description = "Montant prévu au budget en XOF", example = "1000000.00")
    private BigDecimal montantPrevu;

    @Schema(description = "Montant engagé en XOF", example = "500000.00")
    private BigDecimal montantEngage;

    @Schema(description = "Montant consommé en XOF", example = "450000.00")
    private BigDecimal montantConsomme;
}
