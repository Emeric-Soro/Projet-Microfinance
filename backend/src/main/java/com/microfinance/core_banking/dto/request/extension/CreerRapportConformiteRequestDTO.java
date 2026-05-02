package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerRapportConformiteRequestDTO {
    @NotBlank(message = "Le type rapport est obligatoire")
    @Size(max = 50)
    private String typeRapport;

    @NotBlank(message = "La periode debut est obligatoire")
    private String periodeDebut;

    @NotBlank(message = "La periode fin est obligatoire")
    private String periodeFin;

    @NotBlank(message = "Le contenu est obligatoire")
    @Size(max = 5000)
    private String contenu;

    @Size(max = 20)
    private String statut;
}
