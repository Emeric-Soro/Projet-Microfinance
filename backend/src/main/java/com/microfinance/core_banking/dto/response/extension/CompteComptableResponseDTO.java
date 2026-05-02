package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

@Data
public class CompteComptableResponseDTO {
    private Long idCompteComptable;
    private String numeroCompte;
    private String intitule;
    private String typeSolde;
    private Boolean compteInterne;
    private String classe;
    private String agence;
}
