package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerEmployeServiceRequestDTO {
    private String matricule;
    private String nomComplet;
    private String poste;
    private String service;
    private String statut;
    private String dateEmbauche;
    private String email;
    private String telephone;
    private String idAgence;

    public static CreerEmployeServiceRequestDTO fromMap(Map<String, Object> payload) {
        CreerEmployeServiceRequestDTO dto = new CreerEmployeServiceRequestDTO();
        dto.setMatricule(required(payload, "matricule"));
        dto.setNomComplet(required(payload, "nomComplet"));
        dto.setPoste((String) payload.get("poste"));
        dto.setService((String) payload.get("service"));
        Object statutVal = payload.get("statut");
        dto.setStatut(statutVal == null || statutVal.toString().isBlank() ? "ACTIF" : statutVal.toString().trim());
        dto.setDateEmbauche(payload.get("dateEmbauche") == null ? null : payload.get("dateEmbauche").toString());
        dto.setEmail((String) payload.get("email"));
        dto.setTelephone((String) payload.get("telephone"));
        dto.setIdAgence(payload.get("idAgence") == null ? null : payload.get("idAgence").toString());
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
