package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerGrilleScoringRequestDTO {
    @NotBlank(message = "Le code grille est obligatoire")
    private String codeGrille;

    @NotBlank(message = "Le libelle est obligatoire")
    private String libelle;

    @NotNull
    @Positive
    private Integer seuilApprobation;

    @NotNull
    @Positive
    private Integer seuilRejet;

    private Boolean actif;
}
