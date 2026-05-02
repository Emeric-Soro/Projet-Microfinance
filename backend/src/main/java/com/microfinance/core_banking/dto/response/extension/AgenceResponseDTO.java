package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

@Data
public class AgenceResponseDTO {
    private Long idAgence;
    private String codeAgence;
    private String nomAgence;
    private String adresse;
    private String telephone;
    private String statut;
    private String nomRegion;
}
