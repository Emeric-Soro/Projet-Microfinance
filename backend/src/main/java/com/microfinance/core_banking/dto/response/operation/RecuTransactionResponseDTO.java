package com.microfinance.core_banking.dto.response.operation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reçu d'une transaction effectuée")
public class RecuTransactionResponseDTO {

    @Schema(description = "Référence unique de la transaction", example = "TXN-20260401-000001")
    private String referenceUnique;

    @Schema(description = "Type d'opération effectuée", example = "VERSEMENT")
    private String typeOperation;

    @Schema(description = "Montant de la transaction en XOF", example = "15000.00")
    private BigDecimal montant;

    @Schema(description = "Frais appliqués en XOF", example = "500.00")
    private BigDecimal frais;

    @Schema(description = "Date et heure de l'opération", example = "2026-04-01T14:30:00")
    private LocalDateTime dateHeure;

    @Schema(description = "Statut de l'opération", example = "VALIDEE")
    private String statutOperation;

    @Schema(description = "Indique si une validation superviseur est requise", example = "false")
    private Boolean validationSuperviseurRequise;

    @Schema(description = "Date et heure d'exécution effective", example = "2026-04-01T14:30:05")
    private LocalDateTime dateExecution;
}
