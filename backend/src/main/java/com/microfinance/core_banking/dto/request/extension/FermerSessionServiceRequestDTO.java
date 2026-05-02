package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FermerSessionServiceRequestDTO {
    private BigDecimal soldePhysiqueFermeture;
    private BigDecimal soldeTheoriqueFermeture;
    private String commentaire;

    public static FermerSessionServiceRequestDTO fromMap(Map<String, Object> payload) {
        FermerSessionServiceRequestDTO dto = new FermerSessionServiceRequestDTO();
        dto.setSoldePhysiqueFermeture(new BigDecimal(required(payload, "soldePhysiqueFermeture")));
        dto.setSoldeTheoriqueFermeture(payload.get("soldeTheoriqueFermeture") == null ? null : new BigDecimal(payload.get("soldeTheoriqueFermeture").toString()));
        dto.setCommentaire((String) payload.get("commentaire"));
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
