package com.microfinance.core_banking.dto.response.operation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ligne d'un relevé de compte")
public class LigneReleveResponseDTO {

    @Schema(description = "Date et heure de l'opération", example = "2026-04-01T14:30:00")
    private LocalDateTime dateOperation;

    @Schema(description = "Libellé de l'opération", example = "Versement espèces")
    private String libelle;

    @Schema(description = "Sens de l'opération (CREDIT/DEBIT)", example = "CREDIT")
    private String sens;

    @Schema(description = "Montant de l'opération en XOF", example = "15000.00")
    private BigDecimal montant;
}
