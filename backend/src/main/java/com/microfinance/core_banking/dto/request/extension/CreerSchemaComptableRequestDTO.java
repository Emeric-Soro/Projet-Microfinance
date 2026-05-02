package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerSchemaComptableRequestDTO {
    @NotBlank(message = "Le code operation est obligatoire")
    @Size(max = 50)
    private String codeOperation;

    @NotBlank(message = "Le compte debit est obligatoire")
    @Size(max = 20)
    private String compteDebit;

    @NotBlank(message = "Le compte credit est obligatoire")
    @Size(max = 20)
    private String compteCredit;

    @Size(max = 20)
    private String compteFrais;

    @Size(max = 10)
    private String journalCode;

    private Boolean actif;
}
