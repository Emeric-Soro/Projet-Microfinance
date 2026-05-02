package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Impayé constaté sur un crédit")
public class ImpayeCreditResponseDTO {
    @Schema(description = "Identifiant unique de l'impayé", example = "1")
    private Long idImpayeCredit;

    @Schema(description = "Identifiant du crédit associé", example = "1")
    private Long idCredit;

    @Schema(description = "Identifiant de l'échéance concernée", example = "1")
    private Long idEcheanceCredit;

    @Schema(description = "Montant de l'impayé en XOF", example = "50000.00")
    private BigDecimal montant;

    @Schema(description = "Nombre de jours de retard", example = "30")
    private Integer joursRetard;

    @Schema(description = "Classe de risque attribuée", example = "RISQUE_ELEVE")
    private String classeRisque;

    @Schema(description = "Statut de l'impayé", example = "EN_COURS")
    private String statut;
}
