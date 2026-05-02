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
@Schema(description = "Requête de création d'une transaction mobile money")
public class CreerTransactionMobileMoneyRequestDTO {
    @NotNull(message = "L'id wallet source est obligatoire")
    @Schema(description = "Identifiant du wallet source (obligatoire)", example = "1")
    private Long idWalletSource;

    @NotNull(message = "L'id wallet destination est obligatoire")
    @Schema(description = "Identifiant du wallet destination (obligatoire)", example = "2")
    private Long idWalletDestination;

    @NotNull(message = "Le montant est obligatoire")
    @Positive
    @Schema(description = "Montant de la transaction (obligatoire, positif)", example = "25000.00")
    private BigDecimal montant;

    @Size(max = 10)
    @Schema(description = "Devise (optionnel, max 10 caractères)", example = "XOF")
    private String devise;

    @Size(max = 100)
    @Schema(description = "Référence externe (optionnel, max 100 caractères)", example = "EXT-REF-001")
    private String referenceExterne;

    @NotBlank(message = "Le type transaction est obligatoire")
    @Size(max = 50)
    @Schema(description = "Type de transaction (obligatoire, max 50 caractères)", example = "TRANSFERT")
    private String typeTransaction;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "INITIEE")
    private String statut;
}
