package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Action en attente de validation (workflow Maker-Checker)")
public class ActionEnAttenteResponseDTO {
    @Schema(description = "Identifiant unique de l'action en attente", example = "1")
    private Long idActionEnAttente;

    @Schema(description = "Type d'action à valider", example = "CREATION_CLIENT")
    private String typeAction;

    @Schema(description = "Ressource concernée", example = "CLIENT")
    private String ressource;

    @Schema(description = "Référence de la ressource", example = "CLI-20260401-0001")
    private String referenceRessource;

    @Schema(description = "Statut de l'action", example = "EN_ATTENTE")
    private String statut;
}
