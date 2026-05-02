package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerBudgetRequestDTO {
    @NotBlank(message = "Le code budget est obligatoire")
    @Size(max = 20)
    private String codeBudget;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    private String libelle;

    @NotNull(message = "Le montant previsionnel est obligatoire")
    @Positive
    private BigDecimal montantPrevisionnel;

    @NotBlank(message = "L'exercice est obligatoire")
    @Size(max = 20)
    private String exercice;

    private Long idAgence;

    @Size(max = 20)
    private String statut;
}
