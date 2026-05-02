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
public class CreerTransactionMobileMoneyRequestDTO {
    @NotNull(message = "L'id wallet source est obligatoire")
    private Long idWalletSource;

    @NotNull(message = "L'id wallet destination est obligatoire")
    private Long idWalletDestination;

    @NotNull(message = "Le montant est obligatoire")
    @Positive
    private BigDecimal montant;

    @Size(max = 10)
    private String devise;

    @Size(max = 100)
    private String referenceExterne;

    @NotBlank(message = "Le type transaction est obligatoire")
    @Size(max = 50)
    private String typeTransaction;

    @Size(max = 20)
    private String statut;
}
