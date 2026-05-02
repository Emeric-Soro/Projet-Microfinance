package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de calcul des provisions")
public class CalculerProvisionsRequestDTO {
    @Schema(description = "Date de calcul des provisions (optionnel)", example = "2026-04-01")
    private LocalDate dateCalcul;
}
