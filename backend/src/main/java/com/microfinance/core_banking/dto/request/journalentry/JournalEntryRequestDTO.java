package com.microfinance.core_banking.dto.request.journalentry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntryRequestDTO {

    @NotNull(message = "La date d'entrée est obligatoire")
    private LocalDateTime entryDate;

    private Long debitAccountId;
    private Long creditAccountId;

    @NotNull(message = "Le montant est obligatoire")
    private BigDecimal amount;

    private String description;
}
