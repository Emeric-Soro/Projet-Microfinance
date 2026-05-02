package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

@Data
public class EmployeResponseDTO {
    private Long idEmploye;
    private String matricule;
    private String nomComplet;
    private String poste;
    private String service;
    private String statut;
    private String agence;
}
