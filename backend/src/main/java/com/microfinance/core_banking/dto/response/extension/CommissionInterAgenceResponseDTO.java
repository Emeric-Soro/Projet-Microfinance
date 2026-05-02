package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "Commission inter-agence calculée sur une opération déplacée")
public class CommissionInterAgenceResponseDTO {
    @Schema(description = "Identifiant unique de la commission inter-agence", example = "1")
    private Long idCommissionInterAgence;

    @Schema(description = "Identifiant de l'opération déplacée associée", example = "1")
    private Long idOperationDeplacee;

    @Schema(description = "Taux de commission appliqué en pourcentage", example = "1.50")
    private BigDecimal tauxCommission;

    @Schema(description = "Montant de la commission en XOF", example = "1500.00")
    private BigDecimal montantCommission;

    @Schema(description = "Identifiant du compte comptable de commission", example = "1")
    private Long idCompteComptable;

    @Schema(description = "Statut de la commission", example = "COMPTABLE")
    private String statut;

    @Schema(description = "Date de calcul de la commission", example = "2026-04-01")
    private LocalDate dateCalcul;

    @Schema(description = "Date et heure de comptabilisation", example = "2026-04-01T23:00:00")
    private LocalDateTime dateComptabilisation;
}
