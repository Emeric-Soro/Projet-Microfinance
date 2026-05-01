package com.microfinance.core_banking.dto.response.guarantor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuarantorResponseDTO {
    private Long id;
    private Long loanFacilityId;
    private Long guarantorCustomerId;
    private BigDecimal guaranteeAmount;
    private BigDecimal guaranteePercentage;
    private String status;
}
