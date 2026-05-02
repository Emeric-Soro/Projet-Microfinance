package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de déclaration d'incident")
public class DeclarerIncidentServiceRequestDTO {
    @Schema(description = "Référence de l'incident (optionnel)", example = "INC-001")
    private String referenceIncident;
    @Schema(description = "Type d'incident (optionnel)", example = "FRAUDE")
    private String typeIncident;
    @Schema(description = "Gravité (optionnel)", example = "CRITIQUE")
    private String gravite;
    @Schema(description = "Statut (optionnel)", example = "OUVERT")
    private String statut;
    @Schema(description = "Description (optionnel)", example = "Tentative de fraude détectée")
    private String description;
    @Schema(description = "Identifiant du risque associé (optionnel)", example = "1")
    private String idRisque;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static DeclarerIncidentServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        DeclarerIncidentServiceRequestDTO dto = new DeclarerIncidentServiceRequestDTO();
        dto.setReferenceIncident((String) payload.get("referenceIncident"));
        dto.setTypeIncident((String) payload.get("typeIncident"));
        dto.setGravite((String) payload.get("gravite"));
        dto.setStatut((String) payload.get("statut"));
        dto.setDescription((String) payload.get("description"));
        dto.setIdRisque((String) payload.get("idRisque"));
        return dto;
    }
}
