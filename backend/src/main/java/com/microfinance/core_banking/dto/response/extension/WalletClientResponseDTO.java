package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

@Data
public class WalletClientResponseDTO {
    private Long idWalletClient;
    private Long idClient;
    private String client;
    private String operateur;
    private String numeroWallet;
    private String compteSupport;
    private String statut;
}
