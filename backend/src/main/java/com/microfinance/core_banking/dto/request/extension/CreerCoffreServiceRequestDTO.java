package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de création d'un coffre")
public class CreerCoffreServiceRequestDTO {
    @Schema(description = "Identifiant de l'agence (optionnel)", example = "1")
    private String idAgence;
    @Schema(description = "Code du coffre (optionnel)", example = "COF-001")
    private String codeCoffre;
    @Schema(description = "Libellé (optionnel)", example = "Coffre Agence Dakar")
    private String libelle;
    @Schema(description = "Statut (optionnel)", example = "ACTIF")
    private String statut;
    @Schema(description = "Solde théorique (optionnel)", example = "5000000.00")
    private BigDecimal soldeTheorique;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static CreerCoffreServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CreerCoffreServiceRequestDTO dto = new CreerCoffreServiceRequestDTO();
        dto.setIdAgence((String) payload.get("idAgence"));
        dto.setCodeCoffre((String) payload.get("codeCoffre"));
        dto.setLibelle((String) payload.get("libelle"));
        dto.setStatut((String) payload.get("statut"));
        if (payload.get("soldeTheorique") != null) dto.setSoldeTheorique(new java.math.BigDecimal(payload.get("soldeTheorique").toString()));
        return dto;
    }
}
