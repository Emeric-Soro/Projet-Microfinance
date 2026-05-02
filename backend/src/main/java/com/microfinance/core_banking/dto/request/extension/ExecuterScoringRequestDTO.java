package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ExecuterScoringRequestDTO {
    @NotNull(message = "L'id de la demande de credit est obligatoire")
    private Long idDemandeCredit;

    @NotNull(message = "L'id de la grille de scoring est obligatoire")
    private Long idGrilleScoring;
}
