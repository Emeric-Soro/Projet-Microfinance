package com.soutra.microfinance.dto.request.parametrage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgenceRequestDTO {

    @NotBlank
    @Size(max = 20)
    private String codeAgence;

    @NotBlank
    @Size(max = 150)
    private String nom;

    @Size(max = 255)
    private String adresse;

    @Size(max = 30)
    private String telephone;

    @Size(max = 100)
    private String email;

    private Long idChefAgence;
}
