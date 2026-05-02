package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'une demande de crédit")
public class CreerDemandeCreditRequestDTO {
    @NotNull(message = "L'id du client est obligatoire")
    @Schema(description = "Identifiant du client (obligatoire)", example = "1")
    private Long idClient;

    @NotNull(message = "L'id du produit credit est obligatoire")
    @Schema(description = "Identifiant du produit de crédit (obligatoire)", example = "1")
    private Long idProduitCredit;

    @NotNull(message = "Le montant demande est obligatoire")
    @Positive
    @Schema(description = "Montant demandé (obligatoire, positif)", example = "5000000.00")
    private BigDecimal montantDemande;

    @NotNull(message = "La duree en mois est obligatoire")
    @Positive
    @Schema(description = "Durée en mois (obligatoire, positif)", example = "24")
    private Integer dureeMois;

    @Size(max = 500)
    @Schema(description = "Objet du crédit (optionnel, max 500 caractères)", example = "Achat véhicule")
    private String objetCredit;

    @Schema(description = "Score de crédit (optionnel)", example = "75")
    private Integer scoreCredit;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "EN_ATTENTE")
    private String statut;

    @Schema(description = "Identifiant de l'agence (optionnel)", example = "1")
    private Long idAgence;
}
