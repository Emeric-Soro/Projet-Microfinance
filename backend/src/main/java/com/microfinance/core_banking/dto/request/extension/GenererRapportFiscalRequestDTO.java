package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de génération d'un rapport fiscal")
public class GenererRapportFiscalRequestDTO {
    @NotBlank(message = "L'exercice fiscal est obligatoire")
    @Size(max = 20)
    @Schema(description = "Exercice fiscal (obligatoire, max 20 caractères)", example = "2026")
    private String exerciceFiscal;

    @NotBlank(message = "La date d'arreté est obligatoire")
    @Schema(description = "Date d'arrêté (obligatoire)", example = "2026-12-31")
    private String dateArrete;

    @Size(max = 20)
    @Schema(description = "Format du rapport (optionnel, max 20 caractères)", example = "PDF")
    private String format;
}
