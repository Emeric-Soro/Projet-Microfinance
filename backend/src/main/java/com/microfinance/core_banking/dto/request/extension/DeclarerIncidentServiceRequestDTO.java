package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DeclarerIncidentServiceRequestDTO {
    private String referenceIncident;
    private String typeIncident;
    private String gravite;
    private String statut;
    private String description;
    private String idRisque;

    public static DeclarerIncidentServiceRequestDTO fromMap(Map<String, Object> payload) {
        DeclarerIncidentServiceRequestDTO dto = new DeclarerIncidentServiceRequestDTO();
        Object refVal = payload.get("referenceIncident");
        dto.setReferenceIncident(refVal == null || refVal.toString().isBlank() ? null : refVal.toString().trim());
        dto.setTypeIncident(required(payload, "typeIncident"));
        dto.setGravite(required(payload, "gravite"));
        Object statutVal = payload.get("statut");
        dto.setStatut(statutVal == null || statutVal.toString().isBlank() ? "OUVERT" : statutVal.toString().trim());
        dto.setDescription((String) payload.get("description"));
        dto.setIdRisque(payload.get("idRisque") == null ? null : payload.get("idRisque").toString());
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
