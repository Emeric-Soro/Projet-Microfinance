package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Rapport réglementaire généré")
public class RapportReglementaireResponseDTO {
    @Schema(description = "Identifiant unique du rapport réglementaire", example = "1")
    private Long idRapportReglementaire;

    @Schema(description = "Code du rapport", example = "RAP-2026-04-001")
    private String codeRapport;

    @Schema(description = "Type de rapport", example = "BALE")
    private String typeRapport;

    @Schema(description = "Période concernée par le rapport", example = "2026-04")
    private String periode;

    @Schema(description = "Statut du rapport", example = "GENERATED")
    private String statut;

    @Schema(description = "Chemin du fichier généré", example = "/reports/reglementaires/rap_2026_04_001.pdf")
    private String cheminFichier;

    @Schema(description = "Date et heure de génération du rapport", example = "2026-04-30T23:59:59")
    private LocalDateTime dateGeneration;
}
