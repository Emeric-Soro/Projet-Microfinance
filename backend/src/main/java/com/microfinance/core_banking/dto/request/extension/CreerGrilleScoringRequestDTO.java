package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'une grille de scoring")
public class CreerGrilleScoringRequestDTO {
    @NotBlank(message = "Le code grille est obligatoire")
    @Schema(description = "Code de la grille (obligatoire)", example = "GRILLE-001")
    private String codeGrille;

    @NotBlank(message = "Le libelle est obligatoire")
    @Schema(description = "Libellé de la grille (obligatoire)", example = "Grille Scoring Particuliers")
    private String libelle;

    @NotNull
    @Positive
    @Schema(description = "Seuil d'approbation (obligatoire, positif)", example = "70")
    private Integer seuilApprobation;

    @NotNull
    @Positive
    @Schema(description = "Seuil de rejet (obligatoire, positif)", example = "30")
    private Integer seuilRejet;

    @Schema(description = "Grille active (optionnel)", example = "true")
    private Boolean actif;
}
