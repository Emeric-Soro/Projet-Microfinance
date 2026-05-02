package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Mouvement d'un coffre")
public class MouvementCoffreResponseDTO {
    @Schema(description = "Identifiant unique du mouvement de coffre", example = "1")
    private Long idMouvementCoffre;

    @Schema(description = "Type de mouvement", example = "DEPOT")
    private String typeMouvement;

    @Schema(description = "Montant du mouvement en XOF", example = "500000.00")
    private BigDecimal montant;

    @Schema(description = "Référence du mouvement", example = "MVT-20260401-0001")
    private String referenceMouvement;

    @Schema(description = "Commentaire sur le mouvement", example = "Dépôt de fonds quotidien")
    private String commentaire;
}
