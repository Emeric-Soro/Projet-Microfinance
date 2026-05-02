package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerImmobilisationRequestDTO {
    @NotBlank(message = "Le code immobilisation est obligatoire")
    @Size(max = 20)
    private String codeImmobilisation;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    private String libelle;

    @NotBlank(message = "La categorie est obligatoire")
    @Size(max = 50)
    private String categorie;

    @NotNull(message = "La valeur d'acquisition est obligatoire")
    @Positive
    private BigDecimal valeurAcquisition;

    @NotBlank(message = "La date d'acquisition est obligatoire")
    private String dateAcquisition;

    @Size(max = 20)
    private String statut;
}
