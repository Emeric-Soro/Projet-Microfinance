package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DemandeCreditResponseDTO {
    private Long idDemandeCredit;
    private String referenceDossier;
    private Long idClient;
    private String produit;
    private BigDecimal montantDemande;
    private Integer dureeMois;
    private String statut;
    private Integer scoreCredit;
    private String avisComite;
    private String decisionFinale;
    private LocalDateTime dateDecision;
}
