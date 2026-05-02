package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerFournisseurRequestDTO {
    @NotBlank(message = "Le code fournisseur est obligatoire")
    @Size(max = 20)
    private String codeFournisseur;

    @NotBlank(message = "Le nom fournisseur est obligatoire")
    @Size(max = 100)
    private String nomFournisseur;

    @Size(max = 50)
    private String contact;

    @Size(max = 255)
    private String adresse;

    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String statut;
}
