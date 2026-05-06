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
public class FermetureCaisseRequestDTO {

    @NotNull(message = "Le solde physique constate est obligatoire")
    @Positive(message = "Le solde physique constate doit etre positif")
    private BigDecimal soldePhysiqueConstate;
}
