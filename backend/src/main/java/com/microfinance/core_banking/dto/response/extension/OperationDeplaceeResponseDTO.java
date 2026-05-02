package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Opération déplacée entre agences")
public class OperationDeplaceeResponseDTO {
    @Schema(description = "Identifiant unique de l'opération déplacée", example = "1")
    private Long idOperationDeplacee;

    @Schema(description = "Identifiant de la transaction source", example = "1")
    private Long idTransaction;

    @Schema(description = "Référence de la transaction", example = "TXN-20260401-000001")
    private String referenceTransaction;

    @Schema(description = "Agence d'origine", example = "Agence Dakar Plateau")
    private String agenceOrigine;

    @Schema(description = "Agence opérante (destinataire)", example = "Agence Thiès")
    private String agenceOperante;

    @Schema(description = "Type d'opération", example = "VIREMENT")
    private String typeOperation;

    @Schema(description = "Montant de l'opération en XOF", example = "100000.00")
    private BigDecimal montant;

    @Schema(description = "Devise de l'opération", example = "XOF")
    private String devise;

    @Schema(description = "Référence de l'opération", example = "OPD-20260401-0001")
    private String referenceOperation;

    @Schema(description = "Statut de l'opération", example = "COMPTABILISEE")
    private String statut;

    @Schema(description = "Date et heure de l'opération", example = "2026-04-01T10:30:00")
    private LocalDateTime dateOperation;
}
