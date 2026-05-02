package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de règlement de commission agent")
public class ReglerCommissionAgentRequestDTO {
    @NotNull(message = "L'id de l'agent est obligatoire")
    @Schema(description = "Identifiant de l'agent (obligatoire)", example = "1")
    private Long idAgent;

    @NotNull(message = "L'id de la transaction est obligatoire")
    @Schema(description = "Identifiant de la transaction agent (obligatoire)", example = "1")
    private Long idTransactionAgent;

    @NotBlank(message = "Le type de commission est obligatoire")
    @Schema(description = "Type de commission (obligatoire)", example = "DEPOT")
    private String typeCommission;

    @NotNull
    @Positive
    @Schema(description = "Montant de la commission (obligatoire, positif)", example = "5000.00")
    private BigDecimal montantCommission;
}
