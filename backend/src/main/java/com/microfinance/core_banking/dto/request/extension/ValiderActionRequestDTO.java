package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de validation d'une action")
public class ValiderActionRequestDTO {
    @Schema(description = "Statut (optionnel)", example = "APPROUVE")
    private String statut;
    @Schema(description = "Commentaire de validation (optionnel)", example = "Action validée")
    private String commentaireValidation;

    public static ValiderActionRequestDTO fromMap(java.util.Map<String, Object> payload) {
        ValiderActionRequestDTO dto = new ValiderActionRequestDTO();
        dto.setStatut((String) payload.get("statut"));
        Object commentaire = payload.get("commentaireValidation");
        if (commentaire == null) {
            commentaire = payload.get("commentaireChecker");
        }
        if (commentaire == null) {
            commentaire = payload.get("commentaire");
        }
        dto.setCommentaireValidation(commentaire == null ? null : commentaire.toString());
        return dto;
    }
}
