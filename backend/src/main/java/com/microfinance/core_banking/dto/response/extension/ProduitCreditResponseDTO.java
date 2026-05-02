package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Produit de crédit proposé aux clients")
public class ProduitCreditResponseDTO {
    @Schema(description = "Identifiant unique du produit de crédit", example = "1")
    private Long idProduitCredit;

    @Schema(description = "Code du produit", example = "PRET-001")
    private String codeProduit;

    @Schema(description = "Libellé du produit", example = "Prêt personnel")
    private String libelle;

    @Schema(description = "Catégorie du produit", example = "CONSOMMATION")
    private String categorie;

    @Schema(description = "Taux d'intérêt annuel en pourcentage", example = "8.50")
    private BigDecimal tauxAnnuel;

    @Schema(description = "Montant minimal du prêt en XOF", example = "100000.00")
    private BigDecimal montantMin;

    @Schema(description = "Montant maximal du prêt en XOF", example = "5000000.00")
    private BigDecimal montantMax;

    @Schema(description = "Statut du produit", example = "ACTIF")
    private String statut;
}
