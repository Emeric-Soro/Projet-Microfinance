package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerOperateurServiceRequestDTO {
    private String codeOperateur;
    private String nomOperateur;
    private String statut;

    public static CreerOperateurServiceRequestDTO fromMap(Map<String, Object> payload) {
        CreerOperateurServiceRequestDTO dto = new CreerOperateurServiceRequestDTO();
        dto.setCodeOperateur(required(payload, "codeOperateur"));
        dto.setNomOperateur(required(payload, "nomOperateur"));
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
