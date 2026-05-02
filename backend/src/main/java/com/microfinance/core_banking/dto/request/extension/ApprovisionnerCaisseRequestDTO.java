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
@Schema(description = "Requête d'approvisionnement d'une caisse")
public class ApprovisionnerCaisseRequestDTO {
    @NotNull(message = "L'id session caisse est obligatoire")
    @Schema(description = "Identifiant de la session de caisse (obligatoire)", example = "1")
    private Long idSessionCaisse;

    @NotNull(message = "Le montant est obligatoire")
    @Positive
    @Schema(description = "Montant de l'approvisionnement (obligatoire, positif)", example = "200000.00")
    private BigDecimal montant;

    @NotNull(message = "L'id guichetier est obligatoire")
    @Schema(description = "Identifiant du guichetier (obligatoire)", example = "1")
    private Long idGuichetier;

    @Size(max = 500)
    @Schema(description = "Motif (optionnel, max 500 caractères)", example = "Approvisionnement de routine")
    private String motif;
}
