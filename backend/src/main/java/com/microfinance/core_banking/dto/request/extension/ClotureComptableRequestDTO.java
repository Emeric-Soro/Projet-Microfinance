package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de clôture comptable")
public class ClotureComptableRequestDTO {
    @Schema(description = "Date de début de la période (optionnel)", example = "2026-01-01")
    private LocalDate dateDebut;

    @Schema(description = "Date de fin de la période (optionnel)", example = "2026-03-31")
    private LocalDate dateFin;

    @Size(max = 20)
    @Schema(description = "Type de clôture (optionnel, max 20 caractères)", example = "MENSUELLE")
    private String typeCloture;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "EN_COURS")
    private String statut;

    @Size(max = 1000)
    @Schema(description = "Commentaire (optionnel, max 1000 caractères)", example = "Clôture mensuelle T1 2026")
    private String commentaire;
}
