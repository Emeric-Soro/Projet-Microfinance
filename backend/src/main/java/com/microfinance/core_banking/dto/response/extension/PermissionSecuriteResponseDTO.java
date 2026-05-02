package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Permission de sécurité attribuable aux rôles")
public class PermissionSecuriteResponseDTO {
    @Schema(description = "Identifiant unique de la permission", example = "1")
    private Long idPermission;

    @Schema(description = "Code de la permission", example = "CLIENT_CREATE")
    private String codePermission;

    @Schema(description = "Libellé de la permission", example = "Créer un client")
    private String libellePermission;

    @Schema(description = "Code du module concerné", example = "CLIENT")
    private String moduleCode;

    @Schema(description = "Description détaillée de la permission", example = "Permet de créer un nouveau client")
    private String descriptionPermission;

    @Schema(description = "Indique si la permission est active", example = "true")
    private Boolean actif;

    @Schema(description = "Date et heure de création", example = "2026-01-01T00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Date et heure de dernière modification", example = "2026-04-01T14:30:00")
    private LocalDateTime updatedAt;
}
