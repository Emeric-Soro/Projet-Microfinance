package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de report d'échéance d'un crédit")
public class ReportEcheanceCreditRequestDTO {

    @NotNull(message = "L'id du credit est obligatoire")
    @Schema(description = "Identifiant du crédit (obligatoire)", example = "1")
    private Long idCredit;

    @NotNull(message = "L'id de l'echeance est obligatoire")
    @Schema(description = "Identifiant de l'échéance (obligatoire)", example = "1")
    private Long idEcheanceCredit;

    @NotNull(message = "La nouvelle date d'echeance est obligatoire")
    @Schema(description = "Nouvelle date d'échéance (obligatoire)", example = "2026-05-01")
    private LocalDate nouvelleDateEcheance;

    @Size(max = 500, message = "Le commentaire ne doit pas depasser 500 caracteres")
    @Schema(description = "Commentaire (optionnel, max 500 caractères)", example = "Report demandé par le client")
    private String commentaire;
}
