package com.microfinance.core_banking.dto.request.extension;

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
public class RestructurationCreditRequestDTO {

    @NotNull(message = "L'id du credit est obligatoire")
    private Long idCredit;

    @NotNull(message = "La nouvelle duree est obligatoire")
    @Positive(message = "La nouvelle duree doit etre strictement positive")
    private Integer nouvelleDureeMois;

    @PositiveOrZero(message = "Le nouveau taux doit etre positif ou nul")
    private BigDecimal nouveauTauxAnnuel;

    @Size(max = 500, message = "Le commentaire ne doit pas depasser 500 caracteres")
    private String commentaire;
}
