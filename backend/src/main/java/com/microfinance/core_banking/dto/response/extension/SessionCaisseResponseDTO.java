package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Session d'ouverture et fermeture d'une caisse")
public class SessionCaisseResponseDTO {
    @Schema(description = "Identifiant unique de la session de caisse", example = "1")
    private Long idSessionCaisse;

    @Schema(description = "Identifiant de la caisse", example = "1")
    private Long idCaisse;

    @Schema(description = "Identifiant de l'utilisateur (caissier)", example = "1")
    private Long idUtilisateur;

    @Schema(description = "Date et heure d'ouverture", example = "2026-04-01T08:00:00")
    private LocalDateTime dateOuverture;

    @Schema(description = "Date et heure de fermeture", example = "2026-04-01T17:00:00")
    private LocalDateTime dateFermeture;

    @Schema(description = "Solde d'ouverture en XOF", example = "500000.00")
    private BigDecimal soldeOuverture;

    @Schema(description = "Solde théorique de fermeture en XOF", example = "750000.00")
    private BigDecimal soldeTheoriqueFermeture;

    @Schema(description = "Solde physique constaté à la fermeture en XOF", example = "750000.00")
    private BigDecimal soldePhysiqueFermeture;

    @Schema(description = "Écart constaté entre solde théorique et physique en XOF", example = "0.00")
    private BigDecimal ecart;

    @Schema(description = "Statut de la session", example = "FERMEE")
    private String statut;
}
