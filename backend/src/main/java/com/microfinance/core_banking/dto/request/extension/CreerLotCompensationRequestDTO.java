package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Requête de création d'un lot de compensation")
public class CreerLotCompensationRequestDTO {
    @NotBlank(message = "Le code lot est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code du lot (obligatoire, max 20 caractères)", example = "LOT-001")
    private String codeLot;

    @NotBlank(message = "La date compensation est obligatoire")
    @Schema(description = "Date de compensation (obligatoire)", example = "2026-04-01")
    private String dateCompensation;

    @NotNull(message = "Le montant total est obligatoire")
    @Positive
    @Schema(description = "Montant total du lot (obligatoire, positif)", example = "5000000.00")
    private BigDecimal montantTotal;

    @NotNull(message = "Le nombre de transactions est obligatoire")
    @Positive
    @Schema(description = "Nombre de transactions (obligatoire, positif)", example = "150")
    private Integer nombreTransactions;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "INITIE")
    private String statut;
}
