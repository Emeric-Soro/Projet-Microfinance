package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditResponseDTO {
    private Long idCredit;
    private String referenceCredit;
    private Long idClient;
    private BigDecimal montantAccorde;
    private BigDecimal tauxAnnuel;
    private BigDecimal mensualite;
    private BigDecimal capitalRestantDu;
    private BigDecimal fraisPreleves;
    private String referenceTransactionDeblocage;
    private String statut;
}
