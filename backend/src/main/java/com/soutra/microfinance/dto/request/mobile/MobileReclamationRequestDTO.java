package com.soutra.microfinance.dto.request.mobile;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MobileReclamationRequestDTO {

    @NotBlank(message = "Le type de reclamation est obligatoire")
    private String typeReclamation;

    @NotBlank(message = "La description est obligatoire")
    private String description;
}
