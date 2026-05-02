package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RemettreChequeRequestDTO {
    @NotNull(message = "L'id du chequier est obligatoire")
    private Long idChequier;

    @NotBlank(message = "Le numero du cheque est obligatoire")
    private String numeroCheque;

    @NotNull
    @Positive
    private BigDecimal montant;

    @NotBlank(message = "Le tireur est obligatoire")
    private String tireur;

    @NotNull(message = "L'id du compte remise est obligatoire")
    private Long compteRemise;
}
