package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerRisqueRequestDTO {
    @NotBlank(message = "Le code risque est obligatoire")
    @Size(max = 20)
    private String codeRisque;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    private String libelle;

    @NotBlank(message = "La categorie est obligatoire")
    @Size(max = 50)
    private String categorie;

    @NotBlank(message = "La probabilite est obligatoire")
    @Size(max = 20)
    private String probabilite;

    @NotBlank(message = "L'impact est obligatoire")
    @Size(max = 20)
    private String impact;

    @NotBlank(message = "Le niveau risque est obligatoire")
    @Size(max = 20)
    private String niveauRisque;

    @Size(max = 20)
    private String statut;
}
