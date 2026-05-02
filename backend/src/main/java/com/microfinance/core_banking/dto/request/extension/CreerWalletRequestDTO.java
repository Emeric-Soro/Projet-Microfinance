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
@Schema(description = "Requête de création d'un wallet mobile money")
public class CreerWalletRequestDTO {
    @NotBlank(message = "Le code wallet est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code du wallet (obligatoire, max 20 caractères)", example = "WAL-001")
    private String codeWallet;

    @NotNull(message = "L'id operateur est obligatoire")
    @Schema(description = "Identifiant de l'opérateur (obligatoire)", example = "1")
    private Long idOperateur;

    @NotNull(message = "L'id client est obligatoire")
    @Schema(description = "Identifiant du client (obligatoire)", example = "1")
    private Long idClient;

    @Positive
    @Schema(description = "Solde initial (optionnel, positif)", example = "10000.00")
    private BigDecimal soldeInitial;

    @Size(max = 10)
    @Schema(description = "Devise (optionnel, max 10 caractères)", example = "XOF")
    private String devise;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "ACTIF")
    private String statut;
}
