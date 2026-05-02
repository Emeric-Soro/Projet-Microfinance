package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerProduitEpargneRequestDTO {
    @NotBlank(message = "Le code produit est obligatoire")
    @Size(max = 20)
    private String codeProduit;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    private String libelle;

    @Positive
    private BigDecimal tauxRemuneration;

    @Positive
    private BigDecimal montantMin;

    @Positive
    private BigDecimal montantMax;

    private Integer dureeMinMois;

    private Integer dureeMaxMois;

    @Size(max = 1000)
    private String conditions;

    @Size(max = 20)
    private String statut;
}
