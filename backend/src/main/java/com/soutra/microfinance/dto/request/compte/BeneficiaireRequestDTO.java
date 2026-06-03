package com.soutra.microfinance.dto.request.compte;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaireRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String nom;

    @Size(max = 100)
    private String prenom;

    @NotBlank
    @Size(max = 50)
    private String compteBeneficiaire;

    @Size(max = 100)
    private String banque;
}
