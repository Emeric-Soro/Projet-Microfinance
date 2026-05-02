package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Paramètre de configuration d'une agence")
public class ParametreAgenceResponseDTO {
    @Schema(description = "Identifiant unique du paramètre", example = "1")
    private Long idParametreAgence;

    @Schema(description = "Identifiant de l'agence", example = "1")
    private Long idAgence;

    @Schema(description = "Code de l'agence", example = "AG-001")
    private String codeAgence;

    @Schema(description = "Code du paramètre", example = "TAUX_INTERET_EPARGNE")
    private String codeParametre;

    @Schema(description = "Valeur du paramètre", example = "3.50")
    private String valeurParametre;

    @Schema(description = "Type de la valeur", example = "DECIMAL")
    private String typeValeur;

    @Schema(description = "Date d'effet du paramètre", example = "2026-01-01")
    private LocalDate dateEffet;

    @Schema(description = "Date de fin de validité", example = "2026-12-31")
    private LocalDate dateFin;

    @Schema(description = "Version du paramètre", example = "1")
    private Integer versionParametre;

    @Schema(description = "Indique si le paramètre est actif", example = "true")
    private Boolean actif;
}
