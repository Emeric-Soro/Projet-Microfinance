package com.soutra.microfinance.dto.request.credit;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SimulationRequestDTO(

        @NotNull @DecimalMin("1")
        BigDecimal montant,

        @NotNull @DecimalMin("0")
        BigDecimal taux,

        @NotNull @Min(1) @Max(360)
        int duree,

        @NotNull
        String methode
) {}
