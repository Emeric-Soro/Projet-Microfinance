package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerEmployeDigitalRequestDTO {
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50)
    private String nom;

    @NotBlank(message = "Le prenom est obligatoire")
    @Size(max = 50)
    private String prenom;

    @Size(max = 100)
    private String email;

    @Size(max = 50)
    private String telephone;

    @Size(max = 100)
    private String fonction;

    private Long idAgence;

    @Size(max = 20)
    private String statut;
}
