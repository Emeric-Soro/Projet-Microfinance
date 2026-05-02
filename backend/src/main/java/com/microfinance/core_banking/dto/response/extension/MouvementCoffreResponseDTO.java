package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MouvementCoffreResponseDTO {
    private Long idMouvementCoffre;
    private String typeMouvement;
    private BigDecimal montant;
    private String referenceMouvement;
    private String commentaire;
}
