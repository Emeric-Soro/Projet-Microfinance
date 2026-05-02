package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de déclaration d'un incident")
public class DeclarerIncidentRequestDTO {
    @NotBlank(message = "Le type incident est obligatoire")
    @Size(max = 50)
    @Schema(description = "Type d'incident (obligatoire, max 50 caractères)", example = "FRAUDE")
    private String typeIncident;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 1000)
    @Schema(description = "Description de l'incident (obligatoire, max 1000 caractères)", example = "Tentative de fraude détectée")
    private String description;

    @NotBlank(message = "La date incident est obligatoire")
    @Schema(description = "Date de l'incident (obligatoire)", example = "2026-04-01")
    private String dateIncident;

    @Schema(description = "Identifiant du client concerné (optionnel)", example = "1")
    private Long idClient;

    @NotBlank(message = "La gravite est obligatoire")
    @Size(max = 20)
    @Schema(description = "Gravité (obligatoire, max 20 caractères)", example = "CRITIQUE")
    private String gravite;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "OUVERT")
    private String statut;
}
