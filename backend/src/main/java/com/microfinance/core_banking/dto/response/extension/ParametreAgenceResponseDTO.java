package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ParametreAgenceResponseDTO {
    private Long idParametreAgence;
    private Long idAgence;
    private String codeAgence;
    private String codeParametre;
    private String valeurParametre;
    private String typeValeur;
    private LocalDate dateEffet;
    private LocalDate dateFin;
    private Integer versionParametre;
    private Boolean actif;
}
