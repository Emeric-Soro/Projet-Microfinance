package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Ordre de paiement externe (transfert vers un prestataire)")
public class OrdrePaiementExterneResponseDTO {
    @Schema(description = "Identifiant unique de l'ordre de paiement externe", example = "1")
    private Long idOrdrePaiementExterne;

    @Schema(description = "Référence de l'ordre", example = "ORD-20260401-0001")
    private String referenceOrdre;

    @Schema(description = "Type de flux", example = "VIREMENT")
    private String typeFlux;

    @Schema(description = "Sens du flux (EMIS/RECU)", example = "EMIS")
    private String sens;

    @Schema(description = "Montant de l'ordre en XOF", example = "100000.00")
    private BigDecimal montant;

    @Schema(description = "Frais appliqués en XOF", example = "500.00")
    private BigDecimal frais;

    @Schema(description = "Compte débité/crédité", example = "SN12345678901234567890")
    private String compte;

    @Schema(description = "Lot de compensation associé", example = "LOT-20260401-0001")
    private String lotCompensation;

    @Schema(description = "Référence externe fournie par le partenaire", example = "EXT-REF-001")
    private String referenceExterne;

    @Schema(description = "Référence de la transaction interne associée", example = "TXN-20260401-000001")
    private String referenceTransactionInterne;

    @Schema(description = "Détail de la destination", example = "Compte Orange Money 771234567")
    private String destinationDetail;

    @Schema(description = "Date et heure d'initiation", example = "2026-04-01T10:00:00")
    private LocalDateTime dateInitiation;

    @Schema(description = "Date et heure de règlement", example = "2026-04-01T10:05:30")
    private LocalDateTime dateReglement;

    @Schema(description = "Date et heure de rapprochement", example = "2026-04-01T23:00:00")
    private LocalDateTime dateRapprochement;

    @Schema(description = "Statut de l'ordre", example = "EXECUTE")
    private String statut;
}
