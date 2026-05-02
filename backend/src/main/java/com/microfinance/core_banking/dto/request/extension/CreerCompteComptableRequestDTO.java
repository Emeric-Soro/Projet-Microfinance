package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerCompteComptableRequestDTO {
    @NotBlank(message = "Le code classe est obligatoire")
    @Size(max = 10)
    private String codeClasse;

    @NotBlank(message = "Le numero de compte est obligatoire")
    @Size(max = 20)
    private String numeroCompte;

    @NotBlank(message = "L'intitule est obligatoire")
    @Size(max = 200)
    private String intitule;

    @Size(max = 10)
    private String typeSolde;

    private Boolean compteInterne;

    private Long idAgence;
}
