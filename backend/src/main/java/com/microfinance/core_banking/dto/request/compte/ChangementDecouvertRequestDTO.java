package com.microfinance.core_banking.dto.request.compte;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangementDecouvertRequestDTO {

    @NotBlank(message = "Le numero de compte est obligatoire")
    @Size(max = 50, message = "Le numero de compte ne doit pas depasser 50 caracteres")
    private String numCompte;

    @NotNull(message = "Le nouveau plafond est obligatoire")
    @PositiveOrZero(message = "Le nouveau plafond doit etre positif ou nul")
    private BigDecimal nouveauPlafond;
}
