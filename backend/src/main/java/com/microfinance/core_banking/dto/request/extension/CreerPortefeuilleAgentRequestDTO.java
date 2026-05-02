package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerPortefeuilleAgentRequestDTO {
    @NotNull(message = "L'id de l'agent est obligatoire")
    private Long idAgent;

    @NotNull
    @Positive
    private BigDecimal plafondMaximum;

    private BigDecimal plafondMinimum;
}
