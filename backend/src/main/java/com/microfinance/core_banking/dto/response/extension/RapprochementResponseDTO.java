package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "Rapprochement inter-agence")
public class RapprochementResponseDTO {
    @Schema(description = "Identifiant unique du rapprochement inter-agence", example = "1")
    private Long idRapprochementInterAgence;

    @Schema(description = "Agence source de l'opération", example = "Agence Dakar Plateau")
    private String agenceSource;

    @Schema(description = "Agence destination de l'opération", example = "Agence Thiès")
    private String agenceDestination;

    @Schema(description = "Date de début de la période de rapprochement", example = "2026-04-01")
    private LocalDate periodeDebut;

    @Schema(description = "Date de fin de la période de rapprochement", example = "2026-04-30")
    private LocalDate periodeFin;

    @Schema(description = "Montant total au débit en XOF", example = "500000.00")
    private BigDecimal montantDebit;

    @Schema(description = "Montant total au crédit en XOF", example = "500000.00")
    private BigDecimal montantCredit;

    @Schema(description = "Écart constaté en XOF", example = "0.00")
    private BigDecimal ecart;

    @Schema(description = "Statut du rapprochement", example = "APPROUVE")
    private String statut;

    @Schema(description = "Identifiant du validateur", example = "1")
    private Long idValidateur;

    @Schema(description = "Date et heure du rapprochement", example = "2026-04-30T23:00:00")
    private LocalDateTime dateRapprochement;

    @Schema(description = "Commentaire sur le rapprochement", example = "Rapprochement mensuel avril 2026")
    private String commentaire;
}
