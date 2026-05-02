package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de génération d'un rapport prudentiel")
public class GenererRapportPrudentielRequestDTO {
    @NotBlank(message = "La date d'arreté est obligatoire")
    @Schema(description = "Date d'arrêté (obligatoire)", example = "2026-03-31")
    private String dateArrete;

    @Size(max = 50)
    @Schema(description = "Type de périmètre (optionnel, max 50 caractères)", example = "AGENCE")
    private String typePerimetre;

    @Size(max = 20)
    @Schema(description = "Format du rapport (optionnel, max 20 caractères)", example = "PDF")
    private String format;
}
