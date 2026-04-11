package com.microfinance.core_banking.dto.response.operation;

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
public class RecuTransactionResponseDTO {

    private String referenceUnique;
    private String typeOperation;
    private BigDecimal montant;
    private BigDecimal frais;
    private LocalDateTime dateHeure;
}
