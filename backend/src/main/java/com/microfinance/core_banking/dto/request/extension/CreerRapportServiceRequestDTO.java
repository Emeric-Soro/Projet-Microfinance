package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerRapportServiceRequestDTO {
    private String codeRapport;
    private String typeRapport;
    private String periode;
    private String statut;
    private String cheminFichier;
    private String commentaire;

    public static CreerRapportServiceRequestDTO fromMap(Map<String, Object> payload) {
        CreerRapportServiceRequestDTO dto = new CreerRapportServiceRequestDTO();
        dto.setCodeRapport(required(payload, "codeRapport"));
        dto.setTypeRapport(required(payload, "typeRapport"));
        dto.setPeriode(required(payload, "periode"));
        Object statutVal = payload.get("statut");
        dto.setStatut(statutVal == null || statutVal.toString().isBlank() ? "BROUILLON" : statutVal.toString().trim());
        dto.setCheminFichier((String) payload.get("cheminFichier"));
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
