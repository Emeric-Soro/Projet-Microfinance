package com.soutra.microfinance.dto.request.parametrage;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TraiterDerogationRequestDTO {

    @NotBlank(message = "Le statut est obligatoire")
    private String statut;

    @NotBlank(message = "Le motif de traitement est obligatoire")
    private String motifTraitement;
}
