package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Immobilisation (actif à long terme)")
public class ImmobilisationResponseDTO {
    @Schema(description = "Identifiant unique de l'immobilisation", example = "1")
    private Long idImmobilisation;

    @Schema(description = "Code de l'immobilisation", example = "IMM-2026-001")
    private String codeImmobilisation;

    @Schema(description = "Libellé de l'immobilisation", example = "Véhicule utilitaire")
    private String libelle;

    @Schema(description = "Agence rattachée", example = "Agence Dakar Plateau")
    private String agence;

    @Schema(description = "Valeur d'origine en XOF", example = "15000000.00")
    private BigDecimal valeurOrigine;

    @Schema(description = "Durée d'amortissement en mois", example = "60")
    private Integer dureeAmortissementMois;

    @Schema(description = "Montant de l'amortissement mensuel en XOF", example = "250000.00")
    private BigDecimal amortissementMensuel;

    @Schema(description = "Valeur nette comptable en XOF", example = "12500000.00")
    private BigDecimal valeurNette;

    @Schema(description = "Date d'acquisition", example = "2026-01-15")
    private LocalDate dateAcquisition;

    @Schema(description = "Statut de l'immobilisation", example = "ACTIF")
    private String statut;
}
