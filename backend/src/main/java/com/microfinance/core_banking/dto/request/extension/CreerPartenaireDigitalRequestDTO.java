package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerPartenaireDigitalRequestDTO {
    @NotBlank(message = "Le code partenaire est obligatoire")
    @Size(max = 20)
    private String codePartenaire;

    @NotBlank(message = "Le nom partenaire est obligatoire")
    @Size(max = 100)
    private String nomPartenaire;

    @NotBlank(message = "Le type service est obligatoire")
    @Size(max = 50)
    private String typeService;

    @Size(max = 50)
    private String contact;

    @Size(max = 20)
    private String statut;
}
