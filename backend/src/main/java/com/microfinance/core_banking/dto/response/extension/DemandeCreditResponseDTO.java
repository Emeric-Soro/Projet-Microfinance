package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Demande de crédit soumise par un client")
public class DemandeCreditResponseDTO {
    @Schema(description = "Identifiant unique de la demande de crédit", example = "1")
    private Long idDemandeCredit;

    @Schema(description = "Référence du dossier de demande", example = "DOS-20260401-0001")
    private String referenceDossier;

    @Schema(description = "Identifiant du client demandeur", example = "1")
    private Long idClient;

    @Schema(description = "Produit de crédit demandé", example = "PRET_PERSONNEL")
    private String produit;

    @Schema(description = "Montant demandé en XOF", example = "2000000.00")
    private BigDecimal montantDemande;

    @Schema(description = "Durée souhaitée en mois", example = "24")
    private Integer dureeMois;

    @Schema(description = "Statut de la demande", example = "EN_INSTRUCTION")
    private String statut;

    @Schema(description = "Score de crédit attribué", example = "750")
    private Integer scoreCredit;

    @Schema(description = "Avis du comité de crédit", example = "FAVORABLE")
    private String avisComite;

    @Schema(description = "Décision finale", example = "APPROUVE")
    private String decisionFinale;

    @Schema(description = "Date et heure de la décision", example = "2026-04-10T15:30:00")
    private LocalDateTime dateDecision;
}
