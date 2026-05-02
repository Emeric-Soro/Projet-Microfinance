package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

@Data
public class OperateurMobileMoneyResponseDTO {
    private Long idOperateurMobileMoney;
    private String codeOperateur;
    private String nomOperateur;
    private String statut;
}
