package com.microfinance.core_banking.dto.response.journalentry;

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
@Schema(description = "Informations d'une écriture comptable")
public class JournalEntryResponseDTO {
    @Schema(description = "Identifiant unique de l'écriture comptable", example = "1")
    private Long id;

    @Schema(description = "Date et heure de l'écriture comptable", example = "2026-04-01T14:30:00")
    private LocalDateTime entryDate;

    @Schema(description = "Identifiant du compte débité", example = "1")
    private Long debitAccountId;

    @Schema(description = "Identifiant du compte crédité", example = "2")
    private Long creditAccountId;

    @Schema(description = "Montant de l'écriture en XOF", example = "15000.00")
    private BigDecimal amount;

    @Schema(description = "Description de l'écriture comptable", example = "Versement client")
    private String description;
}
