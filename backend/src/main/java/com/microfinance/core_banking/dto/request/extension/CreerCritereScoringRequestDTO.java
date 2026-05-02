package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerCritereScoringRequestDTO {
    @NotBlank(message = "Le code critere est obligatoire")
    private String codeCritere;

    @NotBlank(message = "Le libelle est obligatoire")
    private String libelle;

    @NotBlank(message = "La categorie est obligatoire")
    private String categorie;

    @NotBlank(message = "Le type de valeur est obligatoire")
    private String typeValeur;

    @NotNull
    @Positive
    private BigDecimal poids;

    private Boolean actif;
}
