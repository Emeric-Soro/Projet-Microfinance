package com.microfinance.core_banking.dto.request.operation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class TransactionSimpleRequestDTO {

    @NotBlank(message = "Le numero de compte est obligatoire")
    @Size(max = 50, message = "Le numero de compte ne doit pas depasser 50 caracteres")
    private String numCompte;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit etre strictement positif")
    private BigDecimal montant;

    @NotNull(message = "L'id guichetier est obligatoire")
    private Long idGuichetier;
}
