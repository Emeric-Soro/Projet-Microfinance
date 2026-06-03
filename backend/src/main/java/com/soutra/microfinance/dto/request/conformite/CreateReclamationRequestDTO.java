package com.soutra.microfinance.dto.request.conformite;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReclamationRequestDTO {

    private Long idClient;

    @NotBlank(message = "Le type de réclamation est obligatoire")
    private String typeReclamation;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    private String priorite;

    @NotBlank(message = "Le creePar est obligatoire")
    private String creePar;
}
