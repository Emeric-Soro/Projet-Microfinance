package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Wallet Mobile Money d'un client")
public class WalletClientResponseDTO {
    @Schema(description = "Identifiant unique du wallet client", example = "1")
    private Long idWalletClient;

    @Schema(description = "Identifiant du client propriétaire", example = "1")
    private Long idClient;

    @Schema(description = "Nom du client", example = "John Doe")
    private String client;

    @Schema(description = "Opérateur Mobile Money", example = "Orange Money")
    private String operateur;

    @Schema(description = "Numéro du wallet", example = "+221771234567")
    private String numeroWallet;

    @Schema(description = "Compte interne support", example = "SN12345678901234567890")
    private String compteSupport;

    @Schema(description = "Statut du wallet", example = "ACTIF")
    private String statut;
}
