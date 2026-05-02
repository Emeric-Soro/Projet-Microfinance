package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerCoffreRequestDTO {
    @NotBlank(message = "Le code coffre est obligatoire")
    @Size(max = 20)
    private String codeCoffre;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    private String libelle;

    @NotNull(message = "L'id agence est obligatoire")
    private Long idAgence;

    @Size(max = 20)
    private String statut;
}
