package com.microfinance.core_banking.dto.request.extension;

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
public class CreerCommandeRequestDTO {
    @NotBlank(message = "Le code commande est obligatoire")
    @Size(max = 20)
    private String codeCommande;

    @NotNull(message = "L'id fournisseur est obligatoire")
    private Long idFournisseur;

    @NotBlank(message = "La date commande est obligatoire")
    private String dateCommande;

    @NotNull(message = "Le montant total est obligatoire")
    @Positive
    private BigDecimal montantTotal;

    @Size(max = 20)
    private String statut;
}
