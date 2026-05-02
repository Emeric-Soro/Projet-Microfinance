package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Requête de création d'un budget")
public class CreerBudgetRequestDTO {
    @NotBlank(message = "Le code budget est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code du budget (obligatoire, max 20 caractères)", example = "BUD-2026-001")
    private String codeBudget;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    @Schema(description = "Libellé du budget (obligatoire, max 100 caractères)", example = "Budget fonctionnement 2026")
    private String libelle;

    @NotNull(message = "Le montant previsionnel est obligatoire")
    @Positive
    @Schema(description = "Montant prévisionnel (obligatoire, positif)", example = "50000000.00")
    private BigDecimal montantPrevisionnel;

    @NotBlank(message = "L'exercice est obligatoire")
    @Size(max = 20)
    @Schema(description = "Exercice budgétaire (obligatoire, max 20 caractères)", example = "2026")
    private String exercice;

    @Schema(description = "Identifiant de l'agence (optionnel)", example = "1")
    private Long idAgence;

    @Size(max = 20)
    @Schema(description = "Statut du budget (optionnel, max 20 caractères)", example = "BROUILLON")
    private String statut;
}
