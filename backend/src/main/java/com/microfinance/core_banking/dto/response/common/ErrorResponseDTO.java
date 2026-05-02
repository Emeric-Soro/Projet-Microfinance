package com.microfinance.core_banking.dto.response.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse standard d'erreur de l'API")
public class ErrorResponseDTO {

    @Schema(description = "Date et heure de l'erreur", example = "2026-04-01T14:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Code de statut HTTP", example = "400")
    private int status;

    @Schema(description = "Type d'erreur", example = "Bad Request")
    private String error;

    @Schema(description = "Message détaillé de l'erreur", example = "Validation échouée pour le champ 'email'")
    private String message;

    @Schema(description = "Chemin de la requête ayant généré l'erreur", example = "/api/v1/clients")
    private String path;

    @Schema(description = "Code métier de l'erreur", example = "COMPTE_INTROUVABLE")
    private String code;

    @Schema(description = "Détails supplémentaires de l'erreur", example = "Nom du champ : valeur invalide")
    private String details;

    @Schema(description = "Identifiant de corrélation de la requête", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String correlationId;

    public ErrorResponseDTO(LocalDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
