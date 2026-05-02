package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Résultat détaillé d'un test de stress")
public class ResultatStressTestResponseDTO {
    @Schema(description = "Identifiant unique du résultat de stress test", example = "1")
    private Long idResultatStressTest;

    @Schema(description = "Intitulé du scénario de stress", example = "CRISE_ECONOMIQUE")
    private String stressTest;

    @Schema(description = "Encours de crédit projeté en XOF", example = "500000000.00")
    private BigDecimal encoursCredit;

    @Schema(description = "Pertes projetées en XOF", example = "50000000.00")
    private BigDecimal pertesProjetees;

    @Schema(description = "Retraits projetés en XOF", example = "100000000.00")
    private BigDecimal retraitsProjetes;

    @Schema(description = "Liquidité nette projetée en XOF", example = "350000000.00")
    private BigDecimal liquiditeNette;

    @Schema(description = "Statut du résultat", example = "FAVORABLE")
    private String statutResultat;
}
