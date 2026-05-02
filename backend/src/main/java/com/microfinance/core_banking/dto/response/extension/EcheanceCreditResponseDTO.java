package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Échéance d'un crédit (plan d'amortissement)")
public class EcheanceCreditResponseDTO {
    @Schema(description = "Identifiant unique de l'échéance", example = "1")
    private Long idEcheanceCredit;

    @Schema(description = "Numéro de l'échéance dans le plan", example = "1")
    private Integer numeroEcheance;

    @Schema(description = "Date d'échéance", example = "2026-05-15")
    private LocalDate dateEcheance;

    @Schema(description = "Capital prévu en XOF", example = "50000.00")
    private BigDecimal capitalPrevu;

    @Schema(description = "Intérêts prévus en XOF", example = "10000.00")
    private BigDecimal interetPrevu;

    @Schema(description = "Assurance prévue en XOF", example = "2500.00")
    private BigDecimal assurancePrevue;

    @Schema(description = "Total prévu (capital + intérêts + assurance) en XOF", example = "62500.00")
    private BigDecimal totalPrevu;

    @Schema(description = "Capital payé en XOF", example = "50000.00")
    private BigDecimal capitalPaye;

    @Schema(description = "Intérêts payés en XOF", example = "10000.00")
    private BigDecimal interetPaye;

    @Schema(description = "Assurance payée en XOF", example = "2500.00")
    private BigDecimal assurancePayee;

    @Schema(description = "Statut de l'échéance", example = "PAYEE")
    private String statut;
}
