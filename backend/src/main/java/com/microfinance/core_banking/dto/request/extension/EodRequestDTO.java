package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête d'exécution de la clôture journalière (End of Day)")
public class EodRequestDTO {

    @NotNull(message = "La date de debut est obligatoire")
    @Schema(description = "Date de début de la période (obligatoire)", example = "2026-04-01")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Schema(description = "Date de fin de la période (obligatoire)", example = "2026-04-01")
    private LocalDate dateFin;
}
