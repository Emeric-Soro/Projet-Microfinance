package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de création d'un risque")
public class CreerRisqueServiceRequestDTO {
    @Schema(description = "Code du risque (optionnel)", example = "RISQ-001")
    private String codeRisque;
    @Schema(description = "Catégorie (optionnel)", example = "CREDIT")
    private String categorie;
    @Schema(description = "Libellé (optionnel)", example = "Risque de crédit")
    private String libelle;
    @Schema(description = "Niveau (optionnel)", example = "CRITIQUE")
    private String niveau;
    @Schema(description = "Statut (optionnel)", example = "OUVERT")
    private String statut;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static CreerRisqueServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CreerRisqueServiceRequestDTO dto = new CreerRisqueServiceRequestDTO();
        dto.setCodeRisque((String) payload.get("codeRisque"));
        dto.setCategorie((String) payload.get("categorie"));
        dto.setLibelle((String) payload.get("libelle"));
        dto.setNiveau((String) payload.get("niveau"));
        dto.setStatut((String) payload.get("statut"));
        return dto;
    }
}
