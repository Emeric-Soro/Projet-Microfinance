package com.microfinance.core_banking.dto.request.guarantor;

import com.microfinance.core_banking.entity.Guarantor;
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
public class GuarantorRequestDTO {

    @NotNull(message = "Le loan_facility_id est obligatoire")
    private Long loanFacilityId;

    @NotNull(message = "L identifiant du garant est obligatoire")
    private Long guarantorCustomerId;

    @NotNull(message = "Le montant de garantie est obligatoire")
    private BigDecimal guaranteeAmount;

    private BigDecimal guaranteePercentage;

    @NotNull(message = "Le statut du garant est obligatoire")
    private Guarantor.GuarantorStatus status;
}
