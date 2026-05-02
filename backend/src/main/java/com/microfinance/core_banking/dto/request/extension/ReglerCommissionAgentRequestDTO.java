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
public class ReglerCommissionAgentRequestDTO {
    @NotNull(message = "L'id de l'agent est obligatoire")
    private Long idAgent;

    @NotNull(message = "L'id de la transaction est obligatoire")
    private Long idTransactionAgent;

    @NotBlank(message = "Le type de commission est obligatoire")
    private String typeCommission;

    @NotNull
    @Positive
    private BigDecimal montantCommission;
}
