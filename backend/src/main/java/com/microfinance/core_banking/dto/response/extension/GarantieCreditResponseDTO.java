package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GarantieCreditResponseDTO {
    private Long idGarantieCredit;
    private String typeGarantie;
    private String description;
    private BigDecimal valeur;
    private BigDecimal valeurNantie;
    private String statut;
}
