package com.microfinance.core_banking.dto.response.loan;

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
public class LoanFacilityResponseDTO {

    private Long id;
    private Long customerId;
    private Long productId;
    private BigDecimal principalAmount;
    private BigDecimal outstandingBalance;
    private BigDecimal interestRate;
    private Integer termMonths;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // enum as string
}
