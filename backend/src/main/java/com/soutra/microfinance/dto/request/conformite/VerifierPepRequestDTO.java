package com.soutra.microfinance.dto.request.conformite;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifierPepRequestDTO {

    @NotNull(message = "L'id client est obligatoire")
    private Long idClient;

    @NotBlank(message = "La source d'information est obligatoire")
    private String sourceInformation;

    @NotBlank(message = "Le verifiePar est obligatoire")
    private String verifiePar;
}
