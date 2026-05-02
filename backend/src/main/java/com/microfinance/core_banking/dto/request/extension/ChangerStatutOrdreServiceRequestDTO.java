package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChangerStatutOrdreServiceRequestDTO {
    private String statut;
    private String idLotCompensation;
    private Long idUtilisateurOperateur;

    public static ChangerStatutOrdreServiceRequestDTO fromMap(Map<String, Object> payload) {
        ChangerStatutOrdreServiceRequestDTO dto = new ChangerStatutOrdreServiceRequestDTO();
        dto.setStatut(required(payload, "statut").toUpperCase());
        dto.setIdLotCompensation(payload.get("idLotCompensation") == null ? null : payload.get("idLotCompensation").toString());
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
