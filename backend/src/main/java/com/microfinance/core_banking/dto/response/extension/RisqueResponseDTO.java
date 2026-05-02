package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

@Data
public class RisqueResponseDTO {
    private Long idRisque;
    private String codeRisque;
    private String categorie;
    private String libelle;
    private String niveau;
    private String statut;
}
