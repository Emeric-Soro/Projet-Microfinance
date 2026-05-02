package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerAlerteServiceRequestDTO {
    private String typeAlerte;
    private String niveauRisque;
    private String resume;
    private String details;
    private String statut;
    private String idClient;
    private String idTransaction;

    public static CreerAlerteServiceRequestDTO fromMap(Map<String, Object> payload) {
        CreerAlerteServiceRequestDTO dto = new CreerAlerteServiceRequestDTO();
        dto.setTypeAlerte(required(payload, "typeAlerte"));
        dto.setNiveauRisque(required(payload, "niveauRisque"));
        dto.setResume(required(payload, "resume"));
        dto.setDetails((String) payload.get("details"));
        Object statutVal = payload.get("statut");
        dto.setStatut(statutVal == null || statutVal.toString().isBlank() ? "OUVERTE" : statutVal.toString().trim());
        dto.setIdClient(payload.get("idClient") == null ? null : payload.get("idClient").toString());
        dto.setIdTransaction(payload.get("idTransaction") == null ? null : payload.get("idTransaction").toString());
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
