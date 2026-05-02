package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerStressTestServiceRequestDTO {
    private String codeScenario;
    private String libelle;
    private BigDecimal tauxDefaut;
    private BigDecimal tauxRetrait;
    private String statut;

    public static CreerStressTestServiceRequestDTO fromMap(Map<String, Object> payload) {
        CreerStressTestServiceRequestDTO dto = new CreerStressTestServiceRequestDTO();
        Object codeVal = payload.get("codeScenario");
        dto.setCodeScenario(codeVal == null || codeVal.toString().isBlank() ? null : codeVal.toString().trim());
        dto.setLibelle(required(payload, "libelle"));
        dto.setTauxDefaut(payload.get("tauxDefaut") == null ? BigDecimal.ZERO : new BigDecimal(payload.get("tauxDefaut").toString()));
        dto.setTauxRetrait(payload.get("tauxRetrait") == null ? BigDecimal.ZERO : new BigDecimal(payload.get("tauxRetrait").toString()));
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
