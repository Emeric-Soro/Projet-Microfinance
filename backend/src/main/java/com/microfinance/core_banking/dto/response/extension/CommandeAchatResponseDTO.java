package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Commande d'achat passée auprès d'un fournisseur")
public class CommandeAchatResponseDTO {
    @Schema(description = "Identifiant unique de la commande d'achat", example = "1")
    private Long idCommandeAchat;

    @Schema(description = "Référence de la commande", example = "CMD-20260401-0001")
    private String referenceCommande;

    @Schema(description = "Nom du fournisseur", example = "Fournitures Dakar SARL")
    private String fournisseur;

    @Schema(description = "Agence émettrice", example = "Agence Dakar Plateau")
    private String agence;

    @Schema(description = "Objet de la commande", example = "Fournitures de bureau")
    private String objet;

    @Schema(description = "Montant total de la commande en XOF", example = "250000.00")
    private BigDecimal montant;

    @Schema(description = "Date de la commande", example = "2026-04-01")
    private LocalDate dateCommande;

    @Schema(description = "Statut de la commande", example = "EN_ATTENTE")
    private String statut;
}
