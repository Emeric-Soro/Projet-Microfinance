package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'une action (audit/suivi)")
public class CreerActionRequestDTO {
    @Size(max = 50)
    @Schema(description = "Identifiant du maker (optionnel, max 50 caractères)", example = "USER001")
    private String idMaker;

    @NotBlank(message = "Le type action est obligatoire")
    @Size(max = 50)
    @Schema(description = "Type d'action (obligatoire, max 50 caractères)", example = "CREATION")
    private String typeAction;

    @NotBlank(message = "La ressource est obligatoire")
    @Size(max = 50)
    @Schema(description = "Ressource concernée (obligatoire, max 50 caractères)", example = "CLIENT")
    private String ressource;

    @Size(max = 50)
    @Schema(description = "Référence de la ressource (optionnel, max 50 caractères)", example = "CLI-20260401-0001")
    private String referenceRessource;

    @Schema(description = "Ancienne valeur (optionnel)", example = "{}")
    private String ancienneValeur;

    @Schema(description = "Nouvelle valeur (optionnel)", example = "{}")
    private String nouvelleValeur;

    @Size(max = 500)
    @Schema(description = "Commentaire (optionnel, max 500 caractères)", example = "Création client effectuée")
    private String commentaire;

    public static CreerActionRequestDTO fromMap(Map<String, Object> payload) {
        CreerActionRequestDTO dto = new CreerActionRequestDTO();
        dto.setIdMaker(payload.get("idMaker") == null ? null : payload.get("idMaker").toString());
        dto.setTypeAction(valueOf(payload, "typeAction"));
        dto.setRessource(valueOf(payload, "ressource"));
        dto.setReferenceRessource(payload.get("referenceRessource") == null ? null : payload.get("referenceRessource").toString());
        dto.setAncienneValeur(payload.get("ancienneValeur") == null ? null : payload.get("ancienneValeur").toString());
        dto.setNouvelleValeur(payload.get("nouvelleValeur") == null ? null : payload.get("nouvelleValeur").toString());
        dto.setCommentaire(payload.get("commentaireMaker") == null ? null : payload.get("commentaireMaker").toString());
        return dto;
    }

    private static String valueOf(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }
}
