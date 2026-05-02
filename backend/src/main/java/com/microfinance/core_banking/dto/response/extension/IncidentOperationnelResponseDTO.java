package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Incident opérationnel enregistré")
public class IncidentOperationnelResponseDTO {
    @Schema(description = "Identifiant unique de l'incident", example = "1")
    private Long idIncidentOperationnel;

    @Schema(description = "Référence de l'incident", example = "INC-20260401-0001")
    private String referenceIncident;

    @Schema(description = "Type d'incident", example = "FRAUDE")
    private String typeIncident;

    @Schema(description = "Gravité de l'incident", example = "CRITIQUE")
    private String gravite;

    @Schema(description = "Statut de l'incident", example = "OUVERT")
    private String statut;

    @Schema(description = "Description de l'incident", example = "Tentative de fraude détectée")
    private String description;

    @Schema(description = "Risque associé", example = "Risque de perte financière")
    private String risque;
}
