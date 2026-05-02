package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProduitCreditResponseDTO {
    private Long idProduitCredit;
    private String codeProduit;
    private String libelle;
    private String categorie;
    private BigDecimal tauxAnnuel;
    private BigDecimal montantMin;
    private BigDecimal montantMax;
    private String statut;
}
