package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de souscription à un Dépôt à Terme (DAT)")
public class SouscrireDatRequestDTO {
    @NotNull(message = "L'id client est obligatoire")
    @Schema(description = "Identifiant du client (obligatoire)", example = "1")
    private Long idClient;

    @NotNull(message = "L'id produit epargne est obligatoire")
    @Schema(description = "Identifiant du produit d'épargne (obligatoire)", example = "1")
    private Long idProduitEpargne;

    @NotNull(message = "Le montant de souscription est obligatoire")
    @Positive
    @Schema(description = "Montant de la souscription (obligatoire, positif)", example = "500000.00")
    private BigDecimal montantSouscription;

    @NotNull(message = "La duree en mois est obligatoire")
    @Positive
    @Schema(description = "Durée en mois (obligatoire, positif)", example = "12")
    private Integer dureeMois;

    @Schema(description = "Date de souscription (optionnel)", example = "2026-04-01")
    private String dateSouscription;

    @Schema(description = "Identifiant du guichetier (optionnel)", example = "1")
    private Long idGuichetier;

    @Schema(description = "Renouvellement automatique (optionnel)", example = "true")
    private Boolean renouvellementAuto;

    @Schema(description = "Numéro de compte support (optionnel)", example = "MICR-001-00001")
    private String numCompteSupport;
}
