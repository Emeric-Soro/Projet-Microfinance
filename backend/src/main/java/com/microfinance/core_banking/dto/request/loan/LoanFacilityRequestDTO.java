package com.microfinance.core_banking.dto.request.loan;

import com.microfinance.core_banking.entity.LoanFacility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@Schema(description = "Requête de création d'une facilité de prêt")
public class LoanFacilityRequestDTO {

    @NotNull(message = "Le client est obligatoire")
    @Schema(description = "Identifiant du client (obligatoire)", example = "1")
    private Long customerId;

    @Schema(description = "Identifiant du produit de prêt (optionnel)", example = "1")
    private Long productId;

    @NotNull(message = "Le montant principal est obligatoire")
    @Positive(message = "Le montant principal doit etre positif")
    @Schema(description = "Montant principal du prêt (obligatoire, positif)", example = "1000000.00")
    private BigDecimal principalAmount;

    @NotNull(message = "La duree du terme est obligatoire")
    @Positive(message = "Le terme doit etre positif")
    @Schema(description = "Durée du prêt en mois (obligatoire, positif)", example = "12")
    private Integer termMonths;

    @Schema(description = "Date de début du prêt (optionnel)", example = "2026-04-01")
    private LocalDate startDate;
    @Schema(description = "Date de fin du prêt (optionnel)", example = "2027-04-01")
    private LocalDate endDate;
    @Schema(description = "Statut du prêt (optionnel)", example = "ACTIF")
    private String status;
}
