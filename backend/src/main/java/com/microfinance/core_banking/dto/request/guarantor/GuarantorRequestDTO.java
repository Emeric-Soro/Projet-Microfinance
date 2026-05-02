package com.microfinance.core_banking.dto.request.guarantor;

import com.microfinance.core_banking.entity.Guarantor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête d'ajout d'un garant pour un prêt")
public class GuarantorRequestDTO {

    @NotNull(message = "Le loan_facility_id est obligatoire")
    @Schema(description = "Identifiant du prêt (obligatoire)", example = "1")
    private Long loanFacilityId;

    @NotNull(message = "L identifiant du garant est obligatoire")
    @Schema(description = "Identifiant du client garant (obligatoire)", example = "2")
    private Long guarantorCustomerId;

    @NotNull(message = "Le montant de garantie est obligatoire")
    @Schema(description = "Montant de la garantie (obligatoire)", example = "5000000.00")
    private BigDecimal guaranteeAmount;

    @Schema(description = "Pourcentage de garantie (optionnel)", example = "25.00")
    private BigDecimal guaranteePercentage;

    @NotNull(message = "Le statut du garant est obligatoire")
    @Schema(description = "Statut du garant (obligatoire)", example = "ACTIF")
    private Guarantor.GuarantorStatus status;
}
