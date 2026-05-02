package com.microfinance.core_banking.dto.response.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informations d'une transaction financière")
public class TransactionResponseDTO {
    @Schema(description = "Identifiant unique de la transaction", example = "1")
    private Long id;

    @Schema(description = "Identifiant du compte associé", example = "1")
    private Long accountId;

    @Schema(description = "Identifiant de la facilité de prêt associée", example = "1")
    private Long loanFacilityId;

    @Schema(description = "Montant de la transaction en XOF", example = "15000.00")
    private BigDecimal amount;

    @Schema(description = "Devise de la transaction", example = "XOF")
    private String currency;

    @Schema(description = "Date et heure de la transaction", example = "2026-04-01T14:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Type de transaction", example = "VERSEMENT")
    private String type;

    @Schema(description = "Description de la transaction", example = "Versement espèces client")
    private String description;
}
