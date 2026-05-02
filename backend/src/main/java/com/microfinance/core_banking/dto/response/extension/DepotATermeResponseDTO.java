package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DepotATermeResponseDTO {
    private Long idDepotTerme;
    private String referenceDepot;
    private Long idClient;
    private String produit;
    private BigDecimal montant;
    private BigDecimal tauxApplique;
    private BigDecimal interetsEstimes;
    private LocalDate dateEcheance;
    private String compteSupport;
    private String referenceTransactionSouscription;
    private String statut;
}
