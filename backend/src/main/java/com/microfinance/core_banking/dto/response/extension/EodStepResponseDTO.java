package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Étape d'exécution du processus de fin de journée (EOD)")
public class EodStepResponseDTO {
    @Schema(description = "Nom de l'étape", example = "CALCUL_AGIOS")
    private String stepName;

    @Schema(description = "Statut de l'étape", example = "COMPLETED")
    private String status;

    @Schema(description = "Nombre d'enregistrements lus", example = "150")
    private int readCount;

    @Schema(description = "Nombre d'enregistrements écrits", example = "150")
    private int writeCount;

    @Schema(description = "Nombre de commits effectués", example = "1")
    private int commitCount;

    @Schema(description = "Nombre de rollbacks effectués", example = "0")
    private int rollbackCount;

    @Schema(description = "Date et heure de début de l'étape", example = "2026-04-01T23:00:00")
    private LocalDateTime startTime;

    @Schema(description = "Date et heure de fin de l'étape", example = "2026-04-01T23:05:30")
    private LocalDateTime endTime;
}
