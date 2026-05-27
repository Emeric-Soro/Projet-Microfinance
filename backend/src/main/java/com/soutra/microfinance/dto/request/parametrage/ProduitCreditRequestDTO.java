package com.soutra.microfinance.dto.request.parametrage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduitCreditRequestDTO {

    @NotBlank
    @Size(max = 50)
    private String codeProduit;

    @NotBlank
    @Size(max = 150)
    private String libelle;

    @NotNull
    @Positive
    private BigDecimal tauxInteretAnnuel;

    @NotNull
    @Positive
    private Integer dureeMinMois;

    @NotNull
    @Positive
    private Integer dureeMaxMois;

    @NotNull
    @Positive
    private BigDecimal montantMin;

    @NotNull
    @Positive
    private BigDecimal montantMax;

    private String methodeCalcul;

    private BigDecimal fraisDossierPourcentage;
    private BigDecimal penaliteRetardPourcentage;
}
