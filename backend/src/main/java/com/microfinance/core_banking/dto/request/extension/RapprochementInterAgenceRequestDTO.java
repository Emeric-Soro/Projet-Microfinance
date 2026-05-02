package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de rapprochement inter-agence")
public class RapprochementInterAgenceRequestDTO {
    @NotNull(message = "L'id de l'agence source est obligatoire")
    @Schema(description = "Identifiant de l'agence source (obligatoire)", example = "1")
    private Long idAgenceSource;

    @NotNull(message = "L'id de l'agence destination est obligatoire")
    @Schema(description = "Identifiant de l'agence destination (obligatoire)", example = "2")
    private Long idAgenceDestination;

    @Schema(description = "Période début (optionnel)", example = "2026-01-01")
    private LocalDate periodeDebut;

    @Schema(description = "Période fin (optionnel)", example = "2026-03-31")
    private LocalDate periodeFin;

    @Schema(description = "Montant débit (optionnel)", example = "1000000.00")
    private BigDecimal montantDebit;

    @Schema(description = "Montant crédit (optionnel)", example = "500000.00")
    private BigDecimal montantCredit;

    @Schema(description = "Écart constaté (optionnel)", example = "500000.00")
    private BigDecimal ecart;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "EN_COURS")
    private String statut;

    @Size(max = 500)
    @Schema(description = "Commentaire (optionnel, max 500 caractères)", example = "Rapprochement en cours")
    private String commentaire;

    @Schema(description = "Identifiant du validateur (optionnel)", example = "1")
    private Long idValidateur;
}
