package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerDemandeCreditRequestDTO {
    @NotNull(message = "L'id du client est obligatoire")
    private Long idClient;

    @NotNull(message = "L'id du produit credit est obligatoire")
    private Long idProduitCredit;

    @NotNull(message = "Le montant demande est obligatoire")
    @Positive
    private BigDecimal montantDemande;

    @NotNull(message = "La duree en mois est obligatoire")
    @Positive
    private Integer dureeMois;

    @Size(max = 500)
    private String objetCredit;

    private Integer scoreCredit;

    @Size(max = 20)
    private String statut;

    private Long idAgence;
}
