package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'un critère de scoring")
public class CreerCritereScoringRequestDTO {
    @NotBlank(message = "Le code critere est obligatoire")
    @Schema(description = "Code du critère (obligatoire)", example = "AGE")
    private String codeCritere;

    @NotBlank(message = "Le libelle est obligatoire")
    @Schema(description = "Libellé du critère (obligatoire)", example = "Âge du client")
    private String libelle;

    @NotBlank(message = "La categorie est obligatoire")
    @Schema(description = "Catégorie du critère (obligatoire)", example = "PROFIL")
    private String categorie;

    @NotBlank(message = "Le type de valeur est obligatoire")
    @Schema(description = "Type de valeur (obligatoire)", example = "INTERVALLE")
    private String typeValeur;

    @NotNull
    @Positive
    @Schema(description = "Poids du critère dans le calcul (obligatoire, positif)", example = "20.00")
    private BigDecimal poids;

    @Schema(description = "Critère actif (optionnel)", example = "true")
    private Boolean actif;
}
