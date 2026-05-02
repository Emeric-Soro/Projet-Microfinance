package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GenererBulletinPaieServiceRequestDTO {
    private String idEmploye;
    private String periode;
    private BigDecimal salaireBrut;
    private BigDecimal retenues;
    private BigDecimal salaireNet;
    private String statut;

    public static GenererBulletinPaieServiceRequestDTO fromMap(Map<String, Object> payload) {
        GenererBulletinPaieServiceRequestDTO dto = new GenererBulletinPaieServiceRequestDTO();
        dto.setIdEmploye(required(payload, "idEmploye"));
        dto.setPeriode(required(payload, "periode"));
        dto.setSalaireBrut(payload.get("salaireBrut") == null ? BigDecimal.ZERO : new BigDecimal(payload.get("salaireBrut").toString()));
        dto.setRetenues(payload.get("retenues") == null ? BigDecimal.ZERO : new BigDecimal(payload.get("retenues").toString()));
        dto.setSalaireNet(payload.get("salaireNet") == null ? null : new BigDecimal(payload.get("salaireNet").toString()));
        Object statutVal = payload.get("statut");
        dto.setStatut(statutVal == null || statutVal.toString().isBlank() ? "BROUILLON" : statutVal.toString().trim());
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
