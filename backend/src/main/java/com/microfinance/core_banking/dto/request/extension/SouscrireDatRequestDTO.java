package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SouscrireDatRequestDTO {
    @NotNull(message = "L'id client est obligatoire")
    private Long idClient;

    @NotNull(message = "L'id produit epargne est obligatoire")
    private Long idProduitEpargne;

    @NotNull(message = "Le montant de souscription est obligatoire")
    @Positive
    private BigDecimal montantSouscription;

    @NotNull(message = "La duree en mois est obligatoire")
    @Positive
    private Integer dureeMois;

    private String dateSouscription;

    private Long idGuichetier;
}
