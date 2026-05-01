package com.microfinance.core_banking.dto.response.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {
    private Long id;
    private Long accountId;
    private Long loanFacilityId;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime timestamp;
    private String type;
    private String description;
}
