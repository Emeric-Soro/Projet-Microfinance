package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Alerte de conformité réglementaire")
public class AlerteConformiteResponseDTO {
    @Schema(description = "Identifiant unique de l'alerte", example = "1")
    private Long idAlerteConformite;

    @Schema(description = "Référence de l'alerte", example = "ALR-20260401-0001")
    private String referenceAlerte;

    @Schema(description = "Type d'alerte", example = "NON_CONFORMITE_KYC")
    private String typeAlerte;

    @Schema(description = "Niveau de risque de l'alerte", example = "ELEVE")
    private String niveauRisque;

    @Schema(description = "Statut de l'alerte", example = "OUVERTE")
    private String statut;

    @Schema(description = "Résumé de l'alerte", example = "Document KYC expiré pour le client CL-001")
    private String resume;

    @Schema(description = "Date et heure de détection", example = "2026-04-01T10:00:00")
    private LocalDateTime dateDetection;
}
