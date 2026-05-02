package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service d'approvisionnement de caisse")
public class ApprovisionnerCaisseServiceRequestDTO {
    @Schema(description = "Identifiant du coffre (optionnel)", example = "1")
    private String idCoffre;
    @Schema(description = "Identifiant de la caisse (optionnel)", example = "1")
    private String idCaisse;
    @Schema(description = "Montant (optionnel)", example = "200000.00")
    private BigDecimal montant;
    @Schema(description = "Référence de l'opération (optionnel)", example = "REF-001")
    private String referenceOperation;
    @Schema(description = "Commentaire (optionnel)", example = "Approvisionnement coffre vers caisse")
    private String commentaire;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static ApprovisionnerCaisseServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        ApprovisionnerCaisseServiceRequestDTO dto = new ApprovisionnerCaisseServiceRequestDTO();
        dto.setIdCoffre((String) payload.get("idCoffre"));
        dto.setIdCaisse((String) payload.get("idCaisse"));
        if (payload.get("montant") != null) dto.setMontant(new java.math.BigDecimal(payload.get("montant").toString()));
        dto.setReferenceOperation((String) payload.get("referenceOperation"));
        dto.setCommentaire((String) payload.get("commentaire"));
        return dto;
    }
}
