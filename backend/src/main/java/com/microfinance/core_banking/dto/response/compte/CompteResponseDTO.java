package com.microfinance.core_banking.dto.response.compte;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompteResponseDTO {

    private String numCompte;
    private String typeCompte;
    private BigDecimal solde;
    private String devise;
    private BigDecimal decouvertAutorise;
    private String statut;
}
