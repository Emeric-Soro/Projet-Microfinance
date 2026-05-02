package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ImmobilisationResponseDTO {
    private Long idImmobilisation;
    private String codeImmobilisation;
    private String libelle;
    private String agence;
    private BigDecimal valeurOrigine;
    private Integer dureeAmortissementMois;
    private BigDecimal amortissementMensuel;
    private BigDecimal valeurNette;
    private LocalDate dateAcquisition;
    private String statut;
}
