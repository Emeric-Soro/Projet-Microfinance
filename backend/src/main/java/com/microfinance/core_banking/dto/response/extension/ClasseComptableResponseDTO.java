package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

@Data
public class ClasseComptableResponseDTO {
    private Long idClasseComptable;
    private String codeClasse;
    private String libelle;
    private Integer ordreAffichage;
}
