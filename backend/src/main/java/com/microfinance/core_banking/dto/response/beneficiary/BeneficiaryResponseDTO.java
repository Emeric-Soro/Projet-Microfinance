package com.microfinance.core_banking.dto.response.beneficiary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryResponseDTO {
    private Long id;
    private Long loanFacilityId;
    private String beneficiaryAccount;
    private String beneficiaryName;
    private BigDecimal share;
}
