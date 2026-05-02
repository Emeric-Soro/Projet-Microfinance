package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

@Data
public class CompteLiaisonResponseDTO {
    private Long idCompteLiaisonAgence;
    private String agenceSource;
    private String agenceDestination;
    private Long idCompteComptable;
    private String numeroCompteComptable;
    private String libelle;
    private Boolean actif;
}
