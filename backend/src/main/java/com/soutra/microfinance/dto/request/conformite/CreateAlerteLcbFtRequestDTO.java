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
public class CreateAlerteLcbFtRequestDTO {

    private Long idClient;

    @NotBlank(message = "Le type d'alerte est obligatoire")
    private String typeAlerte;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "Le niveau de risque est obligatoire")
    private String niveauRisque;
}
