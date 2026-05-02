package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Exécution du processus de fin de journée (EOD)")
public class EodExecutionResponseDTO {
    @Schema(description = "Identifiant de l'exécution EOD", example = "1")
    private Long executionId;

    @Schema(description = "Statut global de l'exécution", example = "COMPLETED")
    private String status;

    @Schema(description = "Code de sortie du batch", example = "COMPLETED")
    private String exitCode;

    @Schema(description = "Description détaillée de la sortie", example = "EOD terminé avec succès")
    private String exitDescription;

    @Schema(description = "Nom du job batch", example = "EOD_PROCESSING")
    private String jobName;

    @Schema(description = "Date et heure de début", example = "2026-04-01T23:00:00")
    private LocalDateTime startTime;

    @Schema(description = "Date et heure de fin", example = "2026-04-01T23:10:30")
    private LocalDateTime endTime;

    @Schema(description = "Dernière mise à jour", example = "2026-04-01T23:10:30")
    private LocalDateTime lastUpdated;

    @Schema(description = "Liste des étapes exécutées")
    private List<EodStepResponseDTO> steps;
}
