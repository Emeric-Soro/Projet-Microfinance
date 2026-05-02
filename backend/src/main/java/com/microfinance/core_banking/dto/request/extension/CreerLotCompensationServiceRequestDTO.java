package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de création d'un lot de compensation")
public class CreerLotCompensationServiceRequestDTO {
    @Schema(description = "Référence du lot (optionnel)", example = "LOT-001")
    private String referenceLot;
    @Schema(description = "Type de lot (optionnel)", example = "VIREMENT")
    private String typeLot;
    @Schema(description = "Statut (optionnel)", example = "INITIE")
    private String statut;
    @Schema(description = "Date de traitement (optionnel)", example = "2026-04-01")
    private String dateTraitement;
    @Schema(description = "Commentaire (optionnel)", example = "Lot de compensation mensuel")
    private String commentaire;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static CreerLotCompensationServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CreerLotCompensationServiceRequestDTO dto = new CreerLotCompensationServiceRequestDTO();
        dto.setReferenceLot((String) payload.get("referenceLot"));
        dto.setTypeLot((String) payload.get("typeLot"));
        dto.setStatut((String) payload.get("statut"));
        dto.setDateTraitement((String) payload.get("dateTraitement"));
        dto.setCommentaire((String) payload.get("commentaire"));
        return dto;
    }
}
