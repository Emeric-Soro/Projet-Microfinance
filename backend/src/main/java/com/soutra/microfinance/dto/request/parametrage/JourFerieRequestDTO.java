package com.soutra.microfinance.dto.request.parametrage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JourFerieRequestDTO {

    @NotBlank
    @Size(max = 150)
    private String nom;

    @NotBlank
    @Size(max = 20)
    private String dateJour;

    private Boolean recurrent;

    @Size(max = 2)
    private String pays;
}
