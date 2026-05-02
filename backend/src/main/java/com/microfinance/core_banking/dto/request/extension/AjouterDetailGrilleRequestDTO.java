package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête d'ajout d'un détail à une grille de scoring")
public class AjouterDetailGrilleRequestDTO {
    @NotNull(message = "L'id de la grille est obligatoire")
    @Schema(description = "Identifiant de la grille de scoring (obligatoire)", example = "1")
    private Long idGrilleScoring;

    @NotNull(message = "L'id du critere est obligatoire")
    @Schema(description = "Identifiant du critère de scoring (obligatoire)", example = "1")
    private Long idCritereScoring;

    @Schema(description = "Valeur minimale du critère (optionnel)", example = "0")
    private String valeurMin;

    @Schema(description = "Valeur maximale du critère (optionnel)", example = "100")
    private String valeurMax;

    @NotNull(message = "Les points sont obligatoires")
    @Schema(description = "Points attribués (obligatoire)", example = "10")
    private Integer points;
}
