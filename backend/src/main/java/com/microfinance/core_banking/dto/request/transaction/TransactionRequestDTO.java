package com.microfinance.core_banking.dto.request.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requête de transaction générique")
public class TransactionRequestDTO {

    @NotNull(message = "L identifiant du compte est obligatoire")
    @Schema(description = "Identifiant du compte (obligatoire)", example = "1")
    private Long accountId;

    @Schema(description = "Identifiant du prêt associé (optionnel)", example = "1")
    private Long loanFacilityId;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit etre positif")
    @Schema(description = "Montant de la transaction (obligatoire, positif)", example = "15000.00")
    private BigDecimal amount;

    @Schema(description = "Devise (optionnel)", example = "XOF")
    private String currency;

    @Schema(description = "Horodatage de la transaction (optionnel)", example = "2026-04-01T14:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Type de transaction (DEBIT/CREDIT) (optionnel)", example = "DEBIT")
    private String type;

    @Schema(description = "Description de la transaction (optionnel)", example = "Retrait au guichet")
    private String description;
}
