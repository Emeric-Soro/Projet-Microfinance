package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerAlerteConformiteRequestDTO {
    @NotBlank(message = "Le type alerte est obligatoire")
    @Size(max = 50)
    private String typeAlerte;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 1000)
    private String description;

    @NotBlank(message = "La gravite est obligatoire")
    @Size(max = 20)
    private String gravite;

    private Long idClient;

    @Size(max = 20)
    private String statut;
}
