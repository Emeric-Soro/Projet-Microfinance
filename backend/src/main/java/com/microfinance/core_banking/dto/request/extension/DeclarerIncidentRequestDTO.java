package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DeclarerIncidentRequestDTO {
    @NotBlank(message = "Le type incident est obligatoire")
    @Size(max = 50)
    private String typeIncident;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 1000)
    private String description;

    @NotBlank(message = "La date incident est obligatoire")
    private String dateIncident;

    private Long idClient;

    @NotBlank(message = "La gravite est obligatoire")
    @Size(max = 20)
    private String gravite;

    @Size(max = 20)
    private String statut;
}
