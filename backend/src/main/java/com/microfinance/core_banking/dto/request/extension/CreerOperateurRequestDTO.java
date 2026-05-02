package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerOperateurRequestDTO {
    @NotBlank(message = "Le code operateur est obligatoire")
    @Size(max = 20)
    private String codeOperateur;

    @NotBlank(message = "Le nom operateur est obligatoire")
    @Size(max = 100)
    private String nomOperateur;

    @NotBlank(message = "Le type operateur est obligatoire")
    @Size(max = 50)
    private String typeOperateur;

    @Size(max = 50)
    private String contact;

    @Size(max = 20)
    private String statut;
}
