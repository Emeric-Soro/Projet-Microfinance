package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'une commande")
public class CreerCommandeRequestDTO {
    @NotBlank(message = "Le code commande est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code de la commande (obligatoire, max 20 caractères)", example = "CMD-001")
    private String codeCommande;

    @NotNull(message = "L'id fournisseur est obligatoire")
    @Schema(description = "Identifiant du fournisseur (obligatoire)", example = "1")
    private Long idFournisseur;

    @NotBlank(message = "La date commande est obligatoire")
    @Schema(description = "Date de la commande (obligatoire)", example = "2026-04-01")
    private String dateCommande;

    @NotNull(message = "Le montant total est obligatoire")
    @Positive
    @Schema(description = "Montant total de la commande (obligatoire, positif)", example = "1500000.00")
    private BigDecimal montantTotal;

    @Size(max = 20)
    @Schema(description = "Statut de la commande (optionnel, max 20 caractères)", example = "INITIEE")
    private String statut;
}
