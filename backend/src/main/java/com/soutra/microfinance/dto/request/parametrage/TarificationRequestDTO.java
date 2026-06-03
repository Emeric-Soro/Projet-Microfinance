package com.soutra.microfinance.dto.request.parametrage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarificationRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String code;

    @NotBlank
    @Size(max = 200)
    private String libelle;

    @NotBlank
    @Size(max = 50)
    private String categorie;

    @NotBlank
    @Size(max = 100)
    private String valeur;

    @Size(max = 20)
    private String typeValeur;
}
