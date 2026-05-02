package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerFournisseurServiceRequestDTO {
    private String codeFournisseur;
    private String nom;
    private String contact;
    private String telephone;
    private String email;
    private String statut;

    public static CreerFournisseurServiceRequestDTO fromMap(Map<String, Object> payload) {
        CreerFournisseurServiceRequestDTO dto = new CreerFournisseurServiceRequestDTO();
        Object codeVal = payload.get("codeFournisseur");
        dto.setCodeFournisseur(codeVal == null || codeVal.toString().isBlank() ? null : codeVal.toString().trim());
        dto.setNom(required(payload, "nom"));
        dto.setContact((String) payload.get("contact"));
        dto.setTelephone((String) payload.get("telephone"));
        dto.setEmail((String) payload.get("email"));
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
