package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Provision constituée pour un crédit")
public class ProvisionCreditResponseDTO {
    @Schema(description = "Identifiant unique de la provision", example = "1")
    private Long idProvisionCredit;

    @Schema(description = "Identifiant du crédit associé", example = "1")
    private Long idCredit;

    @Schema(description = "Date de calcul de la provision", example = "2026-04-30")
    private LocalDate dateCalcul;

    @Schema(description = "Taux de provision appliqué en pourcentage", example = "25.00")
    private BigDecimal tauxProvision;

    @Schema(description = "Montant de la provision en XOF", example = "375000.00")
    private BigDecimal montantProvision;

    @Schema(description = "Référence de la pièce comptable de provisionnement", example = "ECR-PROV-20260430-0001")
    private String referencePieceComptable;

    @Schema(description = "Statut de la provision", example = "COMPTABLE")
    private String statut;
}
