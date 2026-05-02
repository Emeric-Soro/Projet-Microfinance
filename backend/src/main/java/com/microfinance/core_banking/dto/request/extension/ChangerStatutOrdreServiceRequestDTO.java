package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de changement de statut d'un ordre")
public class ChangerStatutOrdreServiceRequestDTO {
    @Schema(description = "Statut de l'ordre (optionnel)", example = "VALIDE")
    private String statut;
    @Schema(description = "Identifiant du lot de compensation (optionnel)", example = "1")
    private String idLotCompensation;
    @Schema(description = "Identifiant de l'utilisateur opérateur (optionnel)", example = "1")
    private Long idUtilisateurOperateur;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static ChangerStatutOrdreServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        ChangerStatutOrdreServiceRequestDTO dto = new ChangerStatutOrdreServiceRequestDTO();
        dto.setStatut((String) payload.get("statut"));
        dto.setIdLotCompensation((String) payload.get("idLotCompensation"));
        if (payload.get("idUtilisateurOperateur") != null) dto.setIdUtilisateurOperateur(Long.valueOf(payload.get("idUtilisateurOperateur").toString()));
        return dto;
    }
}
