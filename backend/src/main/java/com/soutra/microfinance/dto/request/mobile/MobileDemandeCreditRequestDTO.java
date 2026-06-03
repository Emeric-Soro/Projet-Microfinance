package com.soutra.microfinance.dto.request.mobile;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MobileDemandeCreditRequestDTO {

    @NotNull(message = "Le code produit est obligatoire")
    private String codeProduitCredit;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit etre positif")
    private BigDecimal montantDemande;

    @NotNull(message = "La duree est obligatoire")
    @Positive(message = "La duree doit etre positive")
    private Integer dureeSouhaitee;

    @NotNull(message = "L'objet du credit est obligatoire")
    private String objetCredit;
}
