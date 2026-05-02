package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CommandeAchatResponseDTO {
    private Long idCommandeAchat;
    private String referenceCommande;
    private String fournisseur;
    private String agence;
    private String objet;
    private BigDecimal montant;
    private LocalDate dateCommande;
    private String statut;
}
