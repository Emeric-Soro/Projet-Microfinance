package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerLotCompensationServiceRequestDTO {
    private String referenceLot;
    private String typeLot;
    private String statut;
    private String dateTraitement;
    private String commentaire;

    public static CreerLotCompensationServiceRequestDTO fromMap(Map<String, Object> payload) {
        CreerLotCompensationServiceRequestDTO dto = new CreerLotCompensationServiceRequestDTO();
        Object refVal = payload.get("referenceLot");
        dto.setReferenceLot(refVal == null || refVal.toString().isBlank() ? null : refVal.toString().trim());
        dto.setTypeLot(required(payload, "typeLot"));
        Object statutVal = payload.get("statut");
        dto.setStatut(statutVal == null || statutVal.toString().isBlank() ? "INITIE" : statutVal.toString().trim());
        dto.setDateTraitement(payload.get("dateTraitement") == null ? null : payload.get("dateTraitement").toString());
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
