package com.soutra.microfinance.dto.request.mobile;

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
public class MobileVirementRequestDTO {

    @NotBlank(message = "Le compte source est obligatoire")
    private String compteSource;

    @NotBlank(message = "Le compte destination est obligatoire")
    private String compteDestination;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit etre positif")
    private BigDecimal montant;

    private String motif;
}
