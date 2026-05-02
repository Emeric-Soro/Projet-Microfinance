package com.microfinance.core_banking.dto.request.journalentry;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de création d'une écriture comptable")
public class JournalEntryRequestDTO {

    @NotNull(message = "La date d'entrée est obligatoire")
    @Schema(description = "Date et heure de l'écriture comptable (obligatoire)", example = "2026-04-01T14:30:00")
    private LocalDateTime entryDate;

    @Schema(description = "Identifiant du compte débité (optionnel)", example = "1")
    private Long debitAccountId;
    @Schema(description = "Identifiant du compte crédité (optionnel)", example = "2")
    private Long creditAccountId;

    @NotNull(message = "Le montant est obligatoire")
    @Schema(description = "Montant de l'écriture (obligatoire)", example = "500000.00")
    private BigDecimal amount;

    @Schema(description = "Description de l'écriture (optionnel)", example = "Intérêts prêt client")
    private String description;
}
