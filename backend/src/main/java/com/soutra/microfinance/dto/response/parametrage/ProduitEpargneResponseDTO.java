package com.soutra.microfinance.dto.response.parametrage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProduitEpargneResponseDTO(
    Long idProduitEpargne,
    String codeProduit,
    String libelle,
    BigDecimal tauxInteretAnnuel,
    BigDecimal montantMinOuverture,
    BigDecimal montantMinSolde,
    Integer dureeMinJours,
    Boolean avecCarte,
    Boolean estActif,
    LocalDateTime createdAt
) {}
