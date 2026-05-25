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
public class ProduitEpargneRequestDTO {

    @NotBlank
    @Size(max = 50)
    private String codeProduit;

    @NotBlank
    @Size(max = 150)
    private String libelle;

    @NotNull
    @Positive
    private BigDecimal tauxInteretAnnuel;

    private BigDecimal montantMinOuverture;
    private BigDecimal penaliteRetraitAnticipe;
    private Integer dureeMinJours;
}
