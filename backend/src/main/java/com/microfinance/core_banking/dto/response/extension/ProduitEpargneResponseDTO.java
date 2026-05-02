package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Produit d'épargne disponible à la souscription")
public class ProduitEpargneResponseDTO {
    @Schema(description = "Identifiant unique du produit d'épargne", example = "1")
    private Long idProduitEpargne;

    @Schema(description = "Code du produit", example = "EPG-001")
    private String codeProduit;

    @Schema(description = "Libellé du produit", example = "Livret A")
    private String libelle;

    @Schema(description = "Catégorie du produit", example = "EPARGNE_REGULIERE")
    private String categorie;

    @Schema(description = "Taux d'intérêt annuel en pourcentage", example = "3.50")
    private BigDecimal tauxInteret;

    @Schema(description = "Fréquence de capitalisation des intérêts", example = "MENSUELLE")
    private String frequenceInteret;

    @Schema(description = "Statut du produit", example = "ACTIF")
    private String statut;
}
