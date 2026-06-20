package com.soutra.microfinance.dto.request.parametrage;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RapportPersonnaliseRequestDTO {

    @NotBlank(message = "Le type de rapport est obligatoire")
    private String type;

    private String dateDebut;

    private String dateFin;

    private List<Long> filtreIds;

    private String format;
}
