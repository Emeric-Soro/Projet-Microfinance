package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'une opération déplacée (inter-agence)")
public class OperationDeplaceeRequestDTO {
    @NotNull(message = "L'id de la transaction est obligatoire")
    @Schema(description = "Identifiant de la transaction (obligatoire)", example = "1")
    private Long idTransaction;

    @Schema(description = "Identifiant de l'agence d'origine (optionnel)", example = "1")
    private Long idAgenceOrigine;

    @Schema(description = "Identifiant de l'agence opérante (optionnel)", example = "2")
    private Long idAgenceOperante;

    @Size(max = 50)
    @Schema(description = "Type d'opération (optionnel, max 50 caractères)", example = "VIREMENT")
    private String typeOperation;

    @Schema(description = "Montant (optionnel)", example = "50000.00")
    private BigDecimal montant;

    @Size(max = 10)
    @Schema(description = "Devise (optionnel, max 10 caractères)", example = "XOF")
    private String devise;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "INITIEE")
    private String statut;

    @Size(max = 50)
    @Schema(description = "Référence de l'opération (optionnel, max 50 caractères)", example = "OP-20260401-001")
    private String referenceOperation;

    @Size(max = 500)
    @Schema(description = "Commentaire (optionnel, max 500 caractères)", example = "Opération inter-agence")
    private String commentaire;

    @Schema(description = "Taux de commission (optionnel)", example = "0.5")
    private BigDecimal tauxCommission;

    @Schema(description = "Montant de la commission (optionnel)", example = "2500.00")
    private BigDecimal montantCommission;

    @Schema(description = "Identifiant du compte comptable (optionnel)", example = "1")
    private Long idCompteComptable;

    @Size(max = 20)
    @Schema(description = "Statut de la commission (optionnel, max 20 caractères)", example = "CALCULEE")
    private String statutCommission;

    @Size(max = 50)
    @Schema(description = "Référence pièce justificative (optionnel, max 50 caractères)", example = "REF-PIECE-001")
    private String referencePiece;

    @Schema(description = "Date de comptabilisation (optionnel)", example = "2026-04-01T14:30:00")
    private LocalDateTime dateComptabilisation;
}
