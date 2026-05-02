package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Résultat de l'initialisation (bootstrap) des données comptables de base")
public class BootstrapResponseDTO {
    @Schema(description = "Nombre de classes comptables créées", example = "8")
    private long classes;

    @Schema(description = "Nombre de comptes comptables créés", example = "150")
    private long comptes;

    @Schema(description = "Nombre de journaux comptables créés", example = "5")
    private long journaux;

    @Schema(description = "Nombre de schémas comptables créés", example = "20")
    private long schemas;
}
