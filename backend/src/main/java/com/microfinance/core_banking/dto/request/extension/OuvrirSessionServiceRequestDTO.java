package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service d'ouverture de session de caisse")
public class OuvrirSessionServiceRequestDTO {
    @Schema(description = "Identifiant de la caisse (optionnel)", example = "1")
    private String idCaisse;
    @Schema(description = "Identifiant de l'utilisateur (optionnel)", example = "1")
    private String idUtilisateur;
    @Schema(description = "Solde d'ouverture (optionnel)", example = "500000.00")
    private BigDecimal soldeOuverture;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static OuvrirSessionServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        OuvrirSessionServiceRequestDTO dto = new OuvrirSessionServiceRequestDTO();
        dto.setIdCaisse((String) payload.get("idCaisse"));
        dto.setIdUtilisateur((String) payload.get("idUtilisateur"));
        if (payload.get("soldeOuverture") != null) dto.setSoldeOuverture(new java.math.BigDecimal(payload.get("soldeOuverture").toString()));
        return dto;
    }
}
