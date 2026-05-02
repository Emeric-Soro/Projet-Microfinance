package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de fermeture de session de caisse")
public class FermerSessionServiceRequestDTO {
    @Schema(description = "Solde physique de fermeture (optionnel)", example = "750000.00")
    private BigDecimal soldePhysiqueFermeture;
    @Schema(description = "Solde théorique de fermeture (optionnel)", example = "750000.00")
    private BigDecimal soldeTheoriqueFermeture;
    @Schema(description = "Commentaire (optionnel)", example = "Session fermée")
    private String commentaire;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static FermerSessionServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        FermerSessionServiceRequestDTO dto = new FermerSessionServiceRequestDTO();
        if (payload.get("soldePhysiqueFermeture") != null) dto.setSoldePhysiqueFermeture(new java.math.BigDecimal(payload.get("soldePhysiqueFermeture").toString()));
        if (payload.get("soldeTheoriqueFermeture") != null) dto.setSoldeTheoriqueFermeture(new java.math.BigDecimal(payload.get("soldeTheoriqueFermeture").toString()));
        dto.setCommentaire((String) payload.get("commentaire"));
        return dto;
    }
}
