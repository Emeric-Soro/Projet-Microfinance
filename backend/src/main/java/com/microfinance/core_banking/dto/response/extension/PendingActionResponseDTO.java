package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Action en attente de validation dans le workflow Maker-Checker")
public class PendingActionResponseDTO {
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

    @Schema(description = "Commentaire du maker (créateur)", example = "Nouveau client à valider")
    private String commentaireMaker;

    @Schema(description = "Commentaire du checker (validateur)", example = "Validation effectuée")
    private String commentaireChecker;

    @Schema(description = "Identifiant du maker (créateur)", example = "1")
    private Long idMaker;

    @Schema(description = "Identifiant du checker (validateur)", example = "2")
    private Long idChecker;

    @Schema(description = "Date et heure de validation", example = "2026-04-01T14:30:00")
    private LocalDateTime dateValidation;

    @Schema(description = "Date et heure de création", example = "2026-04-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Date et heure de dernière modification", example = "2026-04-01T14:30:00")
    private LocalDateTime updatedAt;
}
