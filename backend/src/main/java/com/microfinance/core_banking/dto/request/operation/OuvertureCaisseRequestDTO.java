package com.microfinance.core_banking.dto.request.operation;

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
public class OuvertureCaisseRequestDTO {

    @NotNull(message = "Le solde initial est obligatoire")
    @Positive(message = "Le solde initial doit etre strictement positif")
    private BigDecimal soldeInitial;
}
