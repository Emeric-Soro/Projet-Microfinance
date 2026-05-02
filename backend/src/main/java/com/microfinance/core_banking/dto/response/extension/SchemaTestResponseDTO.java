package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "Résultat du test d'un schéma comptable")
public class SchemaTestResponseDTO {
    @Schema(description = "Code de l'opération testée", example = "VERSEMENT_ESPECES")
    private String codeOperation;

    @Schema(description = "Code du journal comptable", example = "CAIS")
    private String journalCode;

    @Schema(description = "Montant de l'opération testée en XOF", example = "100000.00")
    private BigDecimal montantOperation;

    @Schema(description = "Frais appliqués en XOF", example = "1000.00")
    private BigDecimal frais;

    @Schema(description = "Total des montants au débit en XOF", example = "101000.00")
    private BigDecimal totalDebit;

    @Schema(description = "Total des montants au crédit en XOF", example = "101000.00")
    private BigDecimal totalCredit;

    @Schema(description = "Indique si l'écriture est équilibrée", example = "true")
    private Boolean equilibree;

    @Schema(description = "Lignes détaillées du test")
    private List<LigneSchemaTestDTO> lignes;
}
