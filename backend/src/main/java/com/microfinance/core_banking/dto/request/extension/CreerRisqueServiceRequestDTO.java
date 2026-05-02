package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerRisqueServiceRequestDTO {
    private String codeRisque;
    private String categorie;
    private String libelle;
    private String niveau;
    private String statut;

    public static CreerRisqueServiceRequestDTO fromMap(Map<String, Object> payload) {
        CreerRisqueServiceRequestDTO dto = new CreerRisqueServiceRequestDTO();
        Object codeVal = payload.get("codeRisque");
        dto.setCodeRisque(codeVal == null || codeVal.toString().isBlank() ? null : codeVal.toString().trim());
        dto.setCategorie(required(payload, "categorie"));
        dto.setLibelle(required(payload, "libelle"));
        dto.setNiveau(required(payload, "niveau"));
        Object statutVal = payload.get("statut");
        dto.setStatut(statutVal == null || statutVal.toString().isBlank() ? "OUVERT" : statutVal.toString().trim());
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
