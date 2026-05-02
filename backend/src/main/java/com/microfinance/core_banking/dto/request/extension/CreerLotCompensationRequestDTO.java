package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerLotCompensationRequestDTO {
    @NotBlank(message = "Le code lot est obligatoire")
    @Size(max = 20)
    private String codeLot;

    @NotBlank(message = "La date compensation est obligatoire")
    private String dateCompensation;

    @NotNull(message = "Le montant total est obligatoire")
    @Positive
    private BigDecimal montantTotal;

    @NotNull(message = "Le nombre de transactions est obligatoire")
    @Positive
    private Integer nombreTransactions;

    @Size(max = 20)
    private String statut;
}
