package com.soutra.microfinance.dto.request.parametrage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DerogationRequestDTO {

    @NotBlank(message = "Le type de derogation est obligatoire")
    private String typeDerogation;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotBlank(message = "Le motif est obligatoire")
    private String motif;

    @NotNull(message = "Le montant concerne est obligatoire")
    @Positive(message = "Le montant doit etre strictement positif")
    private BigDecimal montantConcerne;

    private Long idClient;

    private Long idTransaction;
}
