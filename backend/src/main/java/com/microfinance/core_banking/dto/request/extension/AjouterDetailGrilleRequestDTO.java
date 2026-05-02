package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AjouterDetailGrilleRequestDTO {
    @NotNull(message = "L'id de la grille est obligatoire")
    private Long idGrilleScoring;

    @NotNull(message = "L'id du critere est obligatoire")
    private Long idCritereScoring;

    private String valeurMin;

    private String valeurMax;

    @NotNull(message = "Les points sont obligatoires")
    private Integer points;
}
