package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de génération de rapport fiscal")
public class GenererRapportFiscalServiceRequestDTO {
    @Schema(description = "Période (optionnel)", example = "2026")
    private String periode;
    @Schema(description = "Code du rapport (optionnel)", example = "FISCAL-2026")
    private String codeRapport;
    @Schema(description = "Statut (optionnel)", example = "GENERE")
    private String statut;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static GenererRapportFiscalServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        GenererRapportFiscalServiceRequestDTO dto = new GenererRapportFiscalServiceRequestDTO();
        dto.setPeriode((String) payload.get("periode"));
        dto.setCodeRapport((String) payload.get("codeRapport"));
        dto.setStatut((String) payload.get("statut"));
        return dto;
    }
}
