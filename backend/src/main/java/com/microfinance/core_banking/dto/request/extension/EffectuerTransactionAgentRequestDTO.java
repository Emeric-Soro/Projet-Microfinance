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
@Schema(description = "Requête d'effectuer une transaction agent")
public class EffectuerTransactionAgentRequestDTO {
    @NotNull(message = "L'id de l'agent est obligatoire")
    @Schema(description = "Identifiant de l'agent (obligatoire)", example = "1")
    private Long idAgent;

    @NotNull(message = "L'id du client est obligatoire")
    @Schema(description = "Identifiant du client (obligatoire)", example = "1")
    private Long idClient;

    @NotBlank(message = "Le type d'operation est obligatoire")
    @Schema(description = "Type d'opération (obligatoire)", example = "DEPOT")
    private String typeOperation;

    @NotNull
    @Positive
    @Schema(description = "Montant de la transaction (obligatoire, positif)", example = "50000.00")
    private BigDecimal montant;

    @Schema(description = "Frais de la transaction (optionnel)", example = "500.00")
    private BigDecimal frais;
}
