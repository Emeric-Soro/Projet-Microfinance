package com.soutra.microfinance.dto.response.parametrage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProduitCreditResponseDTO(
    Long idProduitCredit,
    String codeProduit,
    String libelle,
    String typeAmortissement,
    BigDecimal tauxInteretAnnuel,
    BigDecimal taegMaximum,
    Integer dureeMinMois,
    Integer dureeMaxMois,
    BigDecimal montantMin,
    BigDecimal montantMax,
    BigDecimal fraisDossierPourcentage,
    BigDecimal assurancePourcentage,
    Boolean estActif,
    Boolean garantieRequise,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
