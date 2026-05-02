package com.microfinance.core_banking.dto.response.guarantor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informations d'un garant associé à une facilité de prêt")
public class GuarantorResponseDTO {
    @Schema(description = "Identifiant unique du garant", example = "1")
    private Long id;

    @Schema(description = "Identifiant de la facilité de prêt associée", example = "1")
    private Long loanFacilityId;

    @Schema(description = "Identifiant du client garant", example = "1")
    private Long guarantorCustomerId;

    @Schema(description = "Montant de la garantie en XOF", example = "500000.00")
    private BigDecimal guaranteeAmount;

    @Schema(description = "Pourcentage de la garantie", example = "25.00")
    private BigDecimal guaranteePercentage;

    @Schema(description = "Statut de la garantie", example = "ACTIF")
    private String status;
}
