package com.microfinance.core_banking.dto.response.tarification;

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
public class AgioResponseDTO {

    private String typeFrais;
    private BigDecimal montant;
    private LocalDate dateCalcul;
    private Boolean estPreleve;
}
