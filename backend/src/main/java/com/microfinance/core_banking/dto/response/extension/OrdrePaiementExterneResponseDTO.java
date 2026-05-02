package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrdrePaiementExterneResponseDTO {
    private Long idOrdrePaiementExterne;
    private String referenceOrdre;
    private String typeFlux;
    private String sens;
    private BigDecimal montant;
    private BigDecimal frais;
    private String compte;
    private String lotCompensation;
    private String referenceExterne;
    private String referenceTransactionInterne;
    private String destinationDetail;
    private LocalDateTime dateInitiation;
    private LocalDateTime dateReglement;
    private LocalDateTime dateRapprochement;
    private String statut;
}
