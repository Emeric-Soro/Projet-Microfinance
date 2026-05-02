package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Scénario de test de stress")
public class StressTestResponseDTO {
    @Schema(description = "Identifiant unique du stress test", example = "1")
    private Long idStressTest;

    @Schema(description = "Code du scénario", example = "SCENARIO_1")
    private String codeScenario;

    @Schema(description = "Libellé du scénario", example = "Crise économique modérée")
    private String libelle;

    @Schema(description = "Taux de défaut projeté en pourcentage", example = "15.00")
    private BigDecimal tauxDefaut;

    @Schema(description = "Taux de retrait projeté en pourcentage", example = "20.00")
    private BigDecimal tauxRetrait;

    @Schema(description = "Statut du scénario", example = "ACTIF")
    private String statut;
}
