package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de génération d'un bulletin de paie")
public class GenererBulletinPaieRequestDTO {
    @NotNull(message = "L'id employe est obligatoire")
    @Schema(description = "Identifiant de l'employé (obligatoire)", example = "1")
    private Long idEmploye;

    @NotBlank(message = "La periode paie est obligatoire")
    @Schema(description = "Période de paie (obligatoire)", example = "2026-04")
    private String periodePaie;

    @Schema(description = "Date d'émission (optionnel)", example = "2026-04-01")
    private String dateEmission;
}
