package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EnregistrerAppareilRequestDTO {
    @NotBlank(message = "Le code appareil est obligatoire")
    @Size(max = 20)
    private String codeAppareil;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    private String libelle;

    @NotBlank(message = "Le type appareil est obligatoire")
    @Size(max = 50)
    private String typeAppareil;

    private Long idAgence;

    @Size(max = 20)
    private String statut;
}
