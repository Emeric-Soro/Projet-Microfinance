package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de création d'une alerte")
public class CreerAlerteServiceRequestDTO {
    @Schema(description = "Type d'alerte (optionnel)", example = "SANCTION")
    private String typeAlerte;
    @Schema(description = "Niveau de risque (optionnel)", example = "CRITIQUE")
    private String niveauRisque;
    @Schema(description = "Résumé de l'alerte (optionnel)", example = "Alerte sanction détectée")
    private String resume;
    @Schema(description = "Détails de l'alerte (optionnel)", example = "Correspondance trouvée sur liste OFAC")
    private String details;
    @Schema(description = "Statut (optionnel)", example = "OUVERTE")
    private String statut;
    @Schema(description = "Identifiant du client (optionnel)", example = "1")
    private String idClient;
    @Schema(description = "Identifiant de la transaction (optionnel)", example = "1")
    private String idTransaction;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static CreerAlerteServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CreerAlerteServiceRequestDTO dto = new CreerAlerteServiceRequestDTO();
        dto.setTypeAlerte((String) payload.get("typeAlerte"));
        dto.setNiveauRisque((String) payload.get("niveauRisque"));
        dto.setResume((String) payload.get("resume"));
        dto.setDetails((String) payload.get("details"));
        dto.setStatut((String) payload.get("statut"));
        dto.setIdClient((String) payload.get("idClient"));
        dto.setIdTransaction((String) payload.get("idTransaction"));
        return dto;
    }
}
