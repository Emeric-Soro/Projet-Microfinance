package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Clôture comptable d'une période")
public class ClotureComptableResponseDTO {
    @Schema(description = "Identifiant unique de la clôture comptable", example = "1")
    private Long idClotureComptable;

    @Schema(description = "Type de clôture", example = "MENSUELLE")
    private String typeCloture;

    @Schema(description = "Date de début de la période", example = "2026-04-01")
    private LocalDate dateDebut;

    @Schema(description = "Date de fin de la période", example = "2026-04-30")
    private LocalDate dateFin;

    @Schema(description = "Nombre total d'écritures comptables concernées", example = "5000")
    private Integer totalEcritures;

    @Schema(description = "Statut de la clôture", example = "CLOTUREE")
    private String statut;

    @Schema(description = "Commentaire sur la clôture", example = "Clôture mensuelle avril 2026")
    private String commentaire;
}
