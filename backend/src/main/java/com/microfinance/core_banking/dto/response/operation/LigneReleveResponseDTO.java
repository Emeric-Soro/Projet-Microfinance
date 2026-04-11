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
public class LigneReleveResponseDTO {

    private LocalDateTime dateOperation;
    private String libelle;
    private String sens;
    private BigDecimal montant;
}
