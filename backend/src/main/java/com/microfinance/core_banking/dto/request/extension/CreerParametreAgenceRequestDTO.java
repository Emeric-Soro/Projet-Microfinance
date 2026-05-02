package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerParametreAgenceRequestDTO {
    @NotNull(message = "L'id de l'agence est obligatoire")
    private Long idAgence;

    @NotBlank(message = "Le code parametre est obligatoire")
    @Size(max = 50)
    private String codeParametre;

    @NotBlank(message = "La valeur du parametre est obligatoire")
    private String valeurParametre;

    @Size(max = 20)
    private String typeValeur;

    @Size(max = 500)
    private String descriptionParametre;

    private LocalDate dateEffet;

    private LocalDate dateFin;

    private Boolean actif;

    private Integer versionParametre;
}
