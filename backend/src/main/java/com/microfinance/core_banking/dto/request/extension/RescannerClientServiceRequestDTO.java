package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RescannerClientServiceRequestDTO {
    private String origine;
    private Boolean sanctionHit;
    private String niveauRisque;
    private String details;

    public static RescannerClientServiceRequestDTO fromMap(Map<String, Object> payload) {
        RescannerClientServiceRequestDTO dto = new RescannerClientServiceRequestDTO();
        Object origineVal = payload.get("origine");
        dto.setOrigine(origineVal == null || origineVal.toString().isBlank() ? "RESCAN" : origineVal.toString().trim());
        dto.setSanctionHit(Boolean.parseBoolean(String.valueOf(payload.getOrDefault("sanctionHit", false))));
        Object niveauVal = payload.get("niveauRisque");
        dto.setNiveauRisque(niveauVal == null || niveauVal.toString().isBlank() ? "CRITIQUE" : niveauVal.toString().trim());
        Object detailsVal = payload.get("details");
        dto.setDetails(detailsVal == null || detailsVal.toString().isBlank() ? null : detailsVal.toString().trim());
        return dto;
    }
}
