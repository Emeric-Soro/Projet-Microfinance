package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProduitEpargneResponseDTO {
    private Long idProduitEpargne;
    private String codeProduit;
    private String libelle;
    private String categorie;
    private BigDecimal tauxInteret;
    private String frequenceInteret;
    private String statut;
}
