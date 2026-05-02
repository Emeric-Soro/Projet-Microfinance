package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de création d'un produit d'épargne")
public class CreerProduitEpargneServiceRequestDTO {
    @Schema(description = "Code du produit (optionnel)", example = "EPARGNE-CLASSE")
    private String codeProduit;
    @Schema(description = "Libellé (optionnel)", example = "Epargne Classique")
    private String libelle;
    @Schema(description = "Catégorie (optionnel)", example = "EPARGNE_CLASSIQUE")
    private String categorie;
    @Schema(description = "Taux d'intérêt (optionnel)", example = "3.50")
    private BigDecimal tauxInteret;
    @Schema(description = "Dépôt initial minimum (optionnel)", example = "10000.00")
    private BigDecimal depotInitialMin;
    @Schema(description = "Solde minimum (optionnel)", example = "5000.00")
    private BigDecimal soldeMinimum;
    @Schema(description = "Fréquence des intérêts (optionnel)", example = "MENSUELLE")
    private String frequenceInteret;
    @Schema(description = "Statut (optionnel)", example = "ACTIF")
    private String statut;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static CreerProduitEpargneServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CreerProduitEpargneServiceRequestDTO dto = new CreerProduitEpargneServiceRequestDTO();
        dto.setCodeProduit((String) payload.get("codeProduit"));
        dto.setLibelle((String) payload.get("libelle"));
        dto.setCategorie((String) payload.get("categorie"));
        if (payload.get("tauxInteret") != null) dto.setTauxInteret(new java.math.BigDecimal(payload.get("tauxInteret").toString()));
        if (payload.get("depotInitialMin") != null) dto.setDepotInitialMin(new java.math.BigDecimal(payload.get("depotInitialMin").toString()));
        if (payload.get("soldeMinimum") != null) dto.setSoldeMinimum(new java.math.BigDecimal(payload.get("soldeMinimum").toString()));
        dto.setFrequenceInteret((String) payload.get("frequenceInteret"));
        dto.setStatut((String) payload.get("statut"));
        return dto;
    }
}
