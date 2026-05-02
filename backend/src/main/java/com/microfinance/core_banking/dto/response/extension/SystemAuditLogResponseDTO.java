package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Journal d'audit système retraçant les actions utilisateurs")
public class SystemAuditLogResponseDTO {
    @Schema(description = "Identifiant unique de l'entrée d'audit", example = "1")
    private Long id;

    @Schema(description = "Nom de l'utilisateur ayant effectué l'action", example = "jdupont")
    private String userName;

    @Schema(description = "Adresse IP de l'utilisateur", example = "192.168.1.100")
    private String ipAddress;

    @Schema(description = "Identifiant de corrélation", example = "corr-abc-123-def")
    private String correlationId;

    @Schema(description = "User-Agent du navigateur", example = "Mozilla/5.0...")
    private String userAgent;

    @Schema(description = "Noms des rôles de l'utilisateur", example = "ADMIN, AGENT")
    private String roleNames;

    @Schema(description = "Code de l'agence", example = "AG-001")
    private String agencyCode;

    @Schema(description = "Action effectuée", example = "LOGIN")
    private String action;

    @Schema(description = "Ressource ciblée", example = "Client")
    private String resource;

    @Schema(description = "Nom du module concerné", example = "CLIENT")
    private String moduleName;

    @Schema(description = "Nom de l'entité concernée", example = "Client")
    private String entityName;

    @Schema(description = "Identifiant de l'entité concernée", example = "1")
    private String entityId;

    @Schema(description = "Méthode HTTP utilisée", example = "POST")
    private String requestMethod;

    @Schema(description = "Chemin de la requête", example = "/api/v1/clients")
    private String requestPath;

    @Schema(description = "Date de l'opération (business date)", example = "2026-04-01")
    private LocalDate businessDate;

    @Schema(description = "Statut de l'action", example = "SUCCESS")
    private String status;

    @Schema(description = "Détails de l'erreur éventuelle", example = "null")
    private String errorDetails;

    @Schema(description = "Raison de l'action", example = "Authentification utilisateur")
    private String reason;

    @Schema(description = "Valeur avant modification (JSON)", example = "null")
    private String beforeValue;

    @Schema(description = "Valeur après modification (JSON)", example = "{\"nom\":\"John\"}")
    private String afterValue;

    @Schema(description = "Date et heure de l'événement", example = "2026-04-01T14:30:00")
    private LocalDateTime timestamp;
}
