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
@Schema(description = "Requête de remise de chèque")
public class RemettreChequeRequestDTO {
    @NotNull(message = "L'id du chequier est obligatoire")
    @Schema(description = "Identifiant du chéquier (obligatoire)", example = "1")
    private Long idChequier;

    @NotBlank(message = "Le numero du cheque est obligatoire")
    @Schema(description = "Numéro du chèque (obligatoire)", example = "CHQ-001-001")
    private String numeroCheque;

    @NotNull
    @Positive
    @Schema(description = "Montant du chèque (obligatoire, positif)", example = "150000.00")
    private BigDecimal montant;

    @NotBlank(message = "Le tireur est obligatoire")
    @Schema(description = "Nom du tireur (obligatoire)", example = "Jean Dupont")
    private String tireur;

    @NotNull(message = "L'id du compte remise est obligatoire")
    @Schema(description = "Identifiant du compte de remise (obligatoire)", example = "1")
    private Long compteRemise;
}
