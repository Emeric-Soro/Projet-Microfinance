package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de création d'une caisse")
public class CreerCaisseServiceRequestDTO {
    @Schema(description = "Identifiant de l'agence (optionnel)", example = "1")
    private String idAgence;
    @Schema(description = "Code de la caisse (optionnel)", example = "CAI-001")
    private String codeCaisse;
    @Schema(description = "Libellé (optionnel)", example = "Caisse Principale")
    private String libelle;
    @Schema(description = "Statut (optionnel)", example = "ACTIVE")
    private String statut;
    @Schema(description = "Solde théorique (optionnel)", example = "1000000.00")
    private BigDecimal soldeTheorique;
    @Schema(description = "Identifiant du guichet (optionnel)", example = "1")
    private String idGuichet;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static CreerCaisseServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CreerCaisseServiceRequestDTO dto = new CreerCaisseServiceRequestDTO();
        dto.setIdAgence((String) payload.get("idAgence"));
        dto.setCodeCaisse((String) payload.get("codeCaisse"));
        dto.setLibelle((String) payload.get("libelle"));
        dto.setStatut((String) payload.get("statut"));
        if (payload.get("soldeTheorique") != null) dto.setSoldeTheorique(new java.math.BigDecimal(payload.get("soldeTheorique").toString()));
        dto.setIdGuichet((String) payload.get("idGuichet"));
        return dto;
    }
}
