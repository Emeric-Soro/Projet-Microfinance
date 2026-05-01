package com.microfinance.core_banking.dto.response.collateral;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollateralResponseDTO {
    private Long id;
    private Long loanFacilityId;
    private String collateralType;
    private String description;
    private BigDecimal value;
    private String lienStatus;
}
