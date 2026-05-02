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
@Schema(description = "Requête de création d'un produit de crédit")
public class CreerProduitCreditRequestDTO {
    @NotBlank(message = "Le code produit est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code du produit (obligatoire, max 20 caractères)", example = "PREST-PERSO")
    private String codeProduit;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    @Schema(description = "Libellé du produit (obligatoire, max 100 caractères)", example = "Prêt Personnel")
    private String libelle;

    @NotBlank(message = "La categorie est obligatoire")
    @Size(max = 50)
    @Schema(description = "Catégorie du produit (obligatoire, max 50 caractères)", example = "CONSOMMATION")
    private String categorie;

    @NotNull(message = "Le taux annuel est obligatoire")
    @Positive
    @Schema(description = "Taux d'intérêt annuel (obligatoire, positif)", example = "8.50")
    private BigDecimal tauxAnnuel;

    @Schema(description = "Durée minimale en mois (optionnel)", example = "6")
    private Integer dureeMinMois;

    @Schema(description = "Durée maximale en mois (optionnel)", example = "60")
    private Integer dureeMaxMois;

    @NotNull(message = "Le montant minimum est obligatoire")
    @Positive
    @Schema(description = "Montant minimum du prêt (obligatoire, positif)", example = "100000.00")
    private BigDecimal montantMin;

    @NotNull(message = "Le montant maximum est obligatoire")
    @Positive
    @Schema(description = "Montant maximum du prêt (obligatoire, positif)", example = "10000000.00")
    private BigDecimal montantMax;

    @Schema(description = "Frais de dossier (optionnel)", example = "5000.00")
    private BigDecimal fraisDossier;

    @Schema(description = "Taux d'assurance (optionnel)", example = "1.00")
    private BigDecimal assuranceTaux;

    @Size(max = 20)
    @Schema(description = "Statut du produit (optionnel, max 20 caractères)", example = "ACTIF")
    private String statut;
}
