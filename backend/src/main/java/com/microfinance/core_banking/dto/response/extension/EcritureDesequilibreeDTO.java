package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Écriture comptable déséquilibrée détectée lors des contrôles")
public class EcritureDesequilibreeDTO {
    @Schema(description = "Identifiant de l'écriture comptable", example = "1")
    private Long idEcritureComptable;

    @Schema(description = "Référence de la pièce comptable", example = "ECR-20260401-0001")
    private String referencePiece;

    @Schema(description = "Date comptable de l'écriture", example = "2026-04-01")
    private LocalDate dateComptable;

    @Schema(description = "Total des montants au débit en XOF", example = "10000.00")
    private BigDecimal debit;

    @Schema(description = "Total des montants au crédit en XOF", example = "9500.00")
    private BigDecimal credit;

    @Schema(description = "Écart constaté entre débit et crédit en XOF", example = "500.00")
    private BigDecimal ecart;
}
