package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "Ligne détaillée d'un test de schéma comptable")
public class LigneSchemaTestDTO {
    @Schema(description = "Numéro de compte comptable", example = "512000")
    private String numeroCompte;

    @Schema(description = "Sens de l'écriture (DEBIT/CREDIT)", example = "DEBIT")
    private String sens;

    @Schema(description = "Montant de la ligne en XOF", example = "100000.00")
    private BigDecimal montant;

    @Schema(description = "Référence de l'auxiliaire", example = "CLI-001")
    private String referenceAuxiliaire;

    @Schema(description = "Libellé de l'auxiliaire", example = "John Doe")
    private String libelleAuxiliaire;
}
