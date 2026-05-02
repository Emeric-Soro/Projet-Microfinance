package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'un portefeuille agent")
public class CreerPortefeuilleAgentRequestDTO {
    @NotNull(message = "L'id de l'agent est obligatoire")
    @Schema(description = "Identifiant de l'agent (obligatoire)", example = "1")
    private Long idAgent;

    @NotNull
    @Positive
    @Schema(description = "Plafond maximum du portefeuille (obligatoire, positif)", example = "5000000.00")
    private BigDecimal plafondMaximum;

    @Schema(description = "Plafond minimum du portefeuille (optionnel)", example = "10000.00")
    private BigDecimal plafondMinimum;
}
