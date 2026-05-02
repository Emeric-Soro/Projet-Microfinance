package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OuvrirSessionServiceRequestDTO {
    private String idCaisse;
    private String idUtilisateur;
    private BigDecimal soldeOuverture;

    public static OuvrirSessionServiceRequestDTO fromMap(Map<String, Object> payload) {
        OuvrirSessionServiceRequestDTO dto = new OuvrirSessionServiceRequestDTO();
        dto.setIdCaisse(required(payload, "idCaisse"));
        dto.setIdUtilisateur(required(payload, "idUtilisateur"));
        dto.setSoldeOuverture(new BigDecimal(required(payload, "soldeOuverture")));
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
