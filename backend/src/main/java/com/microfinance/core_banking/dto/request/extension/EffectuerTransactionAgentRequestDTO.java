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
public class EffectuerTransactionAgentRequestDTO {
    @NotNull(message = "L'id de l'agent est obligatoire")
    private Long idAgent;

    @NotNull(message = "L'id du client est obligatoire")
    private Long idClient;

    @NotBlank(message = "Le type d'operation est obligatoire")
    private String typeOperation;

    @NotNull
    @Positive
    private BigDecimal montant;

    private BigDecimal frais;
}
