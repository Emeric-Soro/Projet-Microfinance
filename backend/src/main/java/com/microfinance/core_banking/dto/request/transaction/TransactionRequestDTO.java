package com.microfinance.core_banking.dto.request.transaction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestDTO {

    @NotNull(message = "L identifiant du compte est obligatoire")
    private Long accountId;

    private Long loanFacilityId;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit etre positif")
    private BigDecimal amount;

    private String currency;

    private LocalDateTime timestamp;

    private String type; // enum value as string, e.g., "DEBIT", "CREDIT"

    private String description;
}
