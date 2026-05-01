package com.microfinance.core_banking.dto.response.journalentry;

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
public class JournalEntryResponseDTO {
    private Long id;
    private LocalDateTime entryDate;
    private Long debitAccountId;
    private Long creditAccountId;
    private BigDecimal amount;
    private String description;
}
