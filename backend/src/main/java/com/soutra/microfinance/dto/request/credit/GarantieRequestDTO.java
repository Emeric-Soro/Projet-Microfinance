package com.soutra.microfinance.dto.request.credit;

import jakarta.validation.constraints.NotBlank;
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
public class GarantieRequestDTO {

    @NotBlank(message = "Le type de garantie est obligatoire")
    private String typeGarantie;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @Positive(message = "La valeur estimee doit etre positive")
    private BigDecimal valeurEstimee;

    private String referenceDocument;
}
