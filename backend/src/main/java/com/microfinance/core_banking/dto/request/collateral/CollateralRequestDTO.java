package com.microfinance.core_banking.dto.request.collateral;

import com.microfinance.core_banking.entity.Collateral;
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
public class CollateralRequestDTO {

    @NotNull(message = "Le loan_facility_id est obligatoire")
    private Long loanFacilityId;

    @NotNull(message = "Le type de collateral est obligatoire")
    private Collateral.CollateralType collateralType;

    private String description;

    @NotNull(message = "La valeur est obligatoire")
    private BigDecimal value;

    @NotNull(message = "Le statut de lien est obligatoire")
    private Collateral.LienStatus lienStatus;
}
