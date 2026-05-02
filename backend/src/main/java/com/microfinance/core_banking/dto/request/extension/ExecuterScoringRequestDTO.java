package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête d'exécution d'un scoring")
public class ExecuterScoringRequestDTO {
    @NotNull(message = "L'id de la demande de credit est obligatoire")
    @Schema(description = "Identifiant de la demande de crédit (obligatoire)", example = "1")
    private Long idDemandeCredit;

    @NotNull(message = "L'id de la grille de scoring est obligatoire")
    @Schema(description = "Identifiant de la grille de scoring (obligatoire)", example = "1")
    private Long idGrilleScoring;
}
