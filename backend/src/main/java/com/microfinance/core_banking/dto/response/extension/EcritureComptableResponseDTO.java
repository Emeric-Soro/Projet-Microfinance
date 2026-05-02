package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Écriture comptable")
public class EcritureComptableResponseDTO {
    @Schema(description = "Identifiant unique de l'écriture comptable", example = "1")
    private Long idEcritureComptable;

    @Schema(description = "Référence de la pièce comptable", example = "ECR-20260401-0001")
    private String referencePiece;

    @Schema(description = "Code du journal comptable", example = "CAIS")
    private String journal;

    @Schema(description = "Date comptable", example = "2026-04-01")
    private LocalDate dateComptable;

    @Schema(description = "Date de valeur", example = "2026-04-01")
    private LocalDate dateValeur;

    @Schema(description = "Libellé de l'écriture", example = "Versement espèces client")
    private String libelle;

    @Schema(description = "Type de source de l'écriture", example = "OPERATION")
    private String sourceType;

    @Schema(description = "Référence de la source", example = "TXN-20260401-000001")
    private String sourceReference;

    @Schema(description = "Statut de l'écriture", example = "VALIDEE")
    private String statut;
}
