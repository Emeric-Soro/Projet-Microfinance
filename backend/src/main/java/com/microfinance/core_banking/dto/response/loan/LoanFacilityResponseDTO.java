package com.microfinance.core_banking.dto.response.loan;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informations d'une facilité de prêt")
public class LoanFacilityResponseDTO {

    @Schema(description = "Identifiant unique de la facilité de prêt", example = "1")
    private Long id;

    @Schema(description = "Identifiant du client emprunteur", example = "1")
    private Long customerId;

    @Schema(description = "Identifiant du produit de prêt", example = "1")
    private Long productId;

    @Schema(description = "Montant principal du prêt en XOF", example = "1500000.00")
    private BigDecimal principalAmount;

    @Schema(description = "Solde restant dû en XOF", example = "750000.00")
    private BigDecimal outstandingBalance;

    @Schema(description = "Taux d'intérêt annuel en pourcentage", example = "8.50")
    private BigDecimal interestRate;

    @Schema(description = "Durée du prêt en mois", example = "24")
    private Integer termMonths;

    @Schema(description = "Date de début du prêt", example = "2026-01-15")
    private LocalDate startDate;

    @Schema(description = "Date de fin du prêt", example = "2028-01-15")
    private LocalDate endDate;

    @Schema(description = "Statut du prêt", example = "ACTIF")
    private String status;
}
