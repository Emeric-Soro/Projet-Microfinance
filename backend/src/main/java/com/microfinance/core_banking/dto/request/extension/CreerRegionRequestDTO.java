package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerRegionRequestDTO {
    @NotBlank(message = "Le code region est obligatoire")
    @Size(max = 20)
    private String codeRegion;

    @NotBlank(message = "Le nom de la region est obligatoire")
    @Size(max = 100)
    private String nomRegion;

    @Size(max = 20)
    private String statut;
}
