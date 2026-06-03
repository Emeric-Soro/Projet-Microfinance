package com.soutra.microfinance.dto.request.parametrage;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EscaladeRequestDTO {

    @NotBlank(message = "Le type d'escalade est obligatoire")
    private String typeEscalade;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    private String niveau = "N1";

    private Long idClient;

    private Long idTransaction;
}
