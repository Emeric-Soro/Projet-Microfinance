package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerClasseComptableRequestDTO {
    @NotBlank(message = "Le code classe est obligatoire")
    @Size(max = 10)
    private String codeClasse;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    private String libelle;

    private Integer ordreAffichage;
}
