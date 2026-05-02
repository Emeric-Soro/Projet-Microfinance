package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GenererRapportFiscalServiceRequestDTO {
    private String periode;
    private String codeRapport;
    private String statut;

    public static GenererRapportFiscalServiceRequestDTO fromMap(Map<String, Object> payload) {
        GenererRapportFiscalServiceRequestDTO dto = new GenererRapportFiscalServiceRequestDTO();
        dto.setPeriode(required(payload, "periode"));
        Object codeVal = payload.get("codeRapport");
        dto.setCodeRapport(codeVal == null || codeVal.toString().isBlank() ? null : codeVal.toString().trim());
        Object statutVal = payload.get("statut");
        dto.setStatut(statutVal == null || statutVal.toString().isBlank() ? "GENERE" : statutVal.toString().trim());
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
