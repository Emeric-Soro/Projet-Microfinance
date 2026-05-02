package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerProduitEpargneServiceRequestDTO {
    private String codeProduit;
    private String libelle;
    private String categorie;
    private BigDecimal tauxInteret;
    private BigDecimal depotInitialMin;
    private BigDecimal soldeMinimum;
    private String frequenceInteret;
    private String statut;

    public static CreerProduitEpargneServiceRequestDTO fromMap(Map<String, Object> payload) {
        CreerProduitEpargneServiceRequestDTO dto = new CreerProduitEpargneServiceRequestDTO();
        dto.setCodeProduit(required(payload, "codeProduit"));
        dto.setLibelle(required(payload, "libelle"));
        dto.setCategorie(required(payload, "categorie"));
        dto.setTauxInteret(payload.get("tauxInteret") == null ? BigDecimal.ZERO : new BigDecimal(payload.get("tauxInteret").toString()));
        dto.setDepotInitialMin(payload.get("depotInitialMin") == null ? BigDecimal.ZERO : new BigDecimal(payload.get("depotInitialMin").toString()));
        dto.setSoldeMinimum(payload.get("soldeMinimum") == null ? BigDecimal.ZERO : new BigDecimal(payload.get("soldeMinimum").toString()));
        dto.setFrequenceInteret((String) payload.get("frequenceInteret"));
        Object statutVal = payload.get("statut");
        dto.setStatut(statutVal == null || statutVal.toString().isBlank() ? "ACTIF" : statutVal.toString().trim());
        return dto;
    }

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }
}
