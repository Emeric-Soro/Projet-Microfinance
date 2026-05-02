package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de fermeture d'une session de caisse")
public class FermerSessionRequestDTO {
    @NotNull(message = "Le solde final est obligatoire")
    @Positive
    @Schema(description = "Solde final de la caisse (obligatoire, positif)", example = "750000.00")
    private BigDecimal soldeFinal;

    @Size(max = 500)
    @Schema(description = "Observation (optionnel, max 500 caractères)", example = "Session fermée sans incident")
    private String observation;

    @Schema(description = "Date de fermeture (optionnel)", example = "2026-04-01")
    private String dateFermeture;
}
