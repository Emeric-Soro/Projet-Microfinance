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
public class CreerProduitCreditRequestDTO {
    @NotBlank(message = "Le code produit est obligatoire")
    @Size(max = 20)
    private String codeProduit;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    private String libelle;

    @NotBlank(message = "La categorie est obligatoire")
    @Size(max = 50)
    private String categorie;

    @NotNull(message = "Le taux annuel est obligatoire")
    @Positive
    private BigDecimal tauxAnnuel;

    private Integer dureeMinMois;

    private Integer dureeMaxMois;

    @NotNull(message = "Le montant minimum est obligatoire")
    @Positive
    private BigDecimal montantMin;

    @NotNull(message = "Le montant maximum est obligatoire")
    @Positive
    private BigDecimal montantMax;

    private BigDecimal fraisDossier;

    private BigDecimal assuranceTaux;

    @Size(max = 20)
    private String statut;
}
