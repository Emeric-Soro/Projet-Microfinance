package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Remboursement effectué sur un crédit")
public class RemboursementCreditResponseDTO {
    @Schema(description = "Identifiant unique du remboursement", example = "1")
    private Long idRemboursementCredit;

    @Schema(description = "Référence du remboursement", example = "REM-20260401-0001")
    private String referenceRemboursement;

    @Schema(description = "Montant total payé en XOF", example = "75000.00")
    private BigDecimal montant;

    @Schema(description = "Part du capital remboursée en XOF", example = "50000.00")
    private BigDecimal capitalPaye;

    @Schema(description = "Part des intérêts payés en XOF", example = "20000.00")
    private BigDecimal interetPaye;

    @Schema(description = "Part de l'assurance payée en XOF", example = "5000.00")
    private BigDecimal assurancePayee;

    @Schema(description = "Référence de la transaction de paiement", example = "TXN-20260401-000001")
    private String referenceTransaction;

    @Schema(description = "Date et heure du paiement", example = "2026-04-01T10:30:00")
    private LocalDateTime datePaiement;

    @Schema(description = "Statut du remboursement", example = "VALIDE")
    private String statut;
}
