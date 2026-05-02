package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête d'ouverture d'une session de caisse")
public class OuvrirSessionRequestDTO {
    @NotNull(message = "L'id caisse est obligatoire")
    @Schema(description = "Identifiant de la caisse (obligatoire)", example = "1")
    private Long idCaisse;

    @NotNull(message = "L'id guichetier est obligatoire")
    @Schema(description = "Identifiant du guichetier (obligatoire)", example = "1")
    private Long idGuichetier;

    @NotNull(message = "Le solde initial est obligatoire")
    @Positive
    @Schema(description = "Solde initial de la caisse (obligatoire, positif)", example = "500000.00")
    private BigDecimal soldeInitial;

    @Schema(description = "Date d'ouverture (optionnel)", example = "2026-04-01")
    private String dateOuverture;
}
