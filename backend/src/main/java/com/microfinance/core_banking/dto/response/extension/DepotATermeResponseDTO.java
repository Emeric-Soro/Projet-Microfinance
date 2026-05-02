package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Dépôt à terme souscrit par un client")
public class DepotATermeResponseDTO {
    @Schema(description = "Identifiant unique du dépôt à terme", example = "1")
    private Long idDepotTerme;

    @Schema(description = "Référence du dépôt", example = "DAT-20260401-0001")
    private String referenceDepot;

    @Schema(description = "Identifiant du client souscripteur", example = "1")
    private Long idClient;

    @Schema(description = "Produit d'épargne associé", example = "DEPOT_TERME_12M")
    private String produit;

    @Schema(description = "Montant déposé en XOF", example = "1000000.00")
    private BigDecimal montant;

    @Schema(description = "Taux d'intérêt appliqué en pourcentage", example = "5.50")
    private BigDecimal tauxApplique;

    @Schema(description = "Intérêts estimés en XOF", example = "55000.00")
    private BigDecimal interetsEstimes;

    @Schema(description = "Date d'échéance", example = "2027-04-01")
    private LocalDate dateEcheance;

    @Schema(description = "Compte de support", example = "SN12345678901234567890")
    private String compteSupport;

    @Schema(description = "Référence de la transaction de souscription", example = "TXN-20260401-000001")
    private String referenceTransactionSouscription;

    @Schema(description = "Statut du dépôt", example = "ACTIF")
    private String statut;
}
