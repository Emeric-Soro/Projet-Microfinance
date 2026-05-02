package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerGuichetRequestDTO {
    @NotBlank(message = "Le code guichet est obligatoire")
    @Size(max = 20)
    private String codeGuichet;

    @NotBlank(message = "Le nom du guichet est obligatoire")
    @Size(max = 100)
    private String nomGuichet;

    @Size(max = 20)
    private String statut;

    @NotNull(message = "L'id de l'agence est obligatoire")
    private Long idAgence;
}
