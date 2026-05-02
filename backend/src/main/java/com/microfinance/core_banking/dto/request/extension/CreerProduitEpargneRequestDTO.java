package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'un produit d'épargne")
public class CreerProduitEpargneRequestDTO {
    @NotBlank(message = "Le code produit est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code du produit (obligatoire, max 20 caractères)", example = "EPARGNE-CLASSE")
    private String codeProduit;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    @Schema(description = "Libellé du produit (obligatoire, max 100 caractères)", example = "Epargne Classique")
    private String libelle;

    @Positive
    @Schema(description = "Taux de rémunération (optionnel, positif)", example = "3.50")
    private BigDecimal tauxRemuneration;

    @Positive
    @Schema(description = "Montant minimum (optionnel, positif)", example = "5000.00")
    private BigDecimal montantMin;

    @Positive
    @Schema(description = "Montant maximum (optionnel, positif)", example = "10000000.00")
    private BigDecimal montantMax;

    @Schema(description = "Durée minimale en mois (optionnel)", example = "1")
    private Integer dureeMinMois;

    @Schema(description = "Durée maximale en mois (optionnel)", example = "120")
    private Integer dureeMaxMois;

    @Size(max = 1000)
    @Schema(description = "Conditions particulières (optionnel, max 1000 caractères)", example = "Retrait possible à tout moment")
    private String conditions;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "ACTIF")
    private String statut;
}
