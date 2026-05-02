package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerAgenceRequestDTO {
    @NotBlank(message = "Le code agence est obligatoire")
    @Size(max = 20)
    private String codeAgence;

    @NotBlank(message = "Le nom de l'agence est obligatoire")
    @Size(max = 100)
    private String nomAgence;

    @Size(max = 255)
    private String adresse;

    @Size(max = 30)
    private String telephone;

    @Size(max = 20)
    private String statut;

    private Long idRegion;
}
