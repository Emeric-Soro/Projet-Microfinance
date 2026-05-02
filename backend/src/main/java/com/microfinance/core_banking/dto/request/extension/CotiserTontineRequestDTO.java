package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de cotisation à une tontine")
public class CotiserTontineRequestDTO {
    @NotNull(message = "L'id du tour est obligatoire")
    @Schema(description = "Identifiant du tour de tontine (obligatoire)", example = "1")
    private Long idTourTontine;

    @NotNull(message = "L'id du participant est obligatoire")
    @Schema(description = "Identifiant du participant (obligatoire)", example = "1")
    private Long idParticipant;

    @NotNull(message = "Le montant cotise est obligatoire")
    @Positive
    @Schema(description = "Montant cotisé (obligatoire, positif)", example = "50000.00")
    private BigDecimal montantCotise;

    @NotNull
    @Schema(description = "Date de cotisation (obligatoire)", example = "2026-04-01")
    private LocalDate dateCotisation;
}
