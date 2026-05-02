package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de création d'un opérateur")
public class CreerOperateurServiceRequestDTO {
    @Schema(description = "Code de l'opérateur (optionnel)", example = "OP-WAVE")
    private String codeOperateur;
    @Schema(description = "Nom de l'opérateur (optionnel)", example = "Wave Sénégal")
    private String nomOperateur;
    @Schema(description = "Statut (optionnel)", example = "ACTIF")
    private String statut;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static CreerOperateurServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CreerOperateurServiceRequestDTO dto = new CreerOperateurServiceRequestDTO();
        dto.setCodeOperateur((String) payload.get("codeOperateur"));
        dto.setNomOperateur((String) payload.get("nomOperateur"));
        dto.setStatut((String) payload.get("statut"));
        return dto;
    }
}
