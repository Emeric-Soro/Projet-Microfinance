package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de restructuration d'un crédit")
public class RestructurationCreditRequestDTO {

    @NotNull(message = "L'id du credit est obligatoire")
    @Schema(description = "Identifiant du crédit (obligatoire)", example = "1")
    private Long idCredit;

    @NotNull(message = "La nouvelle duree est obligatoire")
    @Positive(message = "La nouvelle duree doit etre strictement positive")
    @Schema(description = "Nouvelle durée en mois (obligatoire, positif)", example = "36")
    private Integer nouvelleDureeMois;

    @PositiveOrZero(message = "Le nouveau taux doit etre positif ou nul")
    @Schema(description = "Nouveau taux annuel (optionnel, positif ou nul)", example = "7.50")
    private BigDecimal nouveauTauxAnnuel;

    @Size(max = 500, message = "Le commentaire ne doit pas depasser 500 caracteres")
    @Schema(description = "Commentaire (optionnel, max 500 caractères)", example = "Restructuration pour difficultés financières")
    private String commentaire;
}
