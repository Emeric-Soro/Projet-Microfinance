package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Ligne du grand livre comptable")
public class LigneGrandLivreDTO {
    @Schema(description = "Date comptable de l'opération", example = "2026-04-01")
    private LocalDate dateComptable;

    @Schema(description = "Référence de la pièce comptable", example = "ECR-20260401-0001")
    private String referencePiece;

    @Schema(description = "Libellé de l'opération", example = "Versement client")
    private String libelle;

    @Schema(description = "Sens de l'écriture (DEBIT/CREDIT)", example = "DEBIT")
    private String sens;

    @Schema(description = "Montant de l'opération en XOF", example = "15000.00")
    private BigDecimal montant;

    @Schema(description = "Solde cumulé après cette opération en XOF", example = "15000.00")
    private BigDecimal solde;

    @Schema(description = "Type de source de l'opération", example = "OPERATION")
    private String sourceType;

    @Schema(description = "Référence de la source", example = "TXN-20260401-000001")
    private String sourceReference;
}
