package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ValiderMutationRequestDTO {
    private String decision;
    private String commentaireValidation;
    private Long idValidateur;
}
