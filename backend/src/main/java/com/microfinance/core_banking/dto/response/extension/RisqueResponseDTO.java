package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Risque identifié dans le système")
public class RisqueResponseDTO {
    @Schema(description = "Identifiant unique du risque", example = "1")
    private Long idRisque;

    @Schema(description = "Code du risque", example = "RISK-001")
    private String codeRisque;

    @Schema(description = "Catégorie du risque", example = "CREDIT")
    private String categorie;

    @Schema(description = "Libellé du risque", example = "Risque de défaut de remboursement")
    private String libelle;

    @Schema(description = "Niveau du risque", example = "ELEVE")
    private String niveau;

    @Schema(description = "Statut du risque", example = "ACTIF")
    private String statut;
}
