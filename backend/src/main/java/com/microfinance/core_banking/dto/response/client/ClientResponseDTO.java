package com.microfinance.core_banking.dto.response.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDTO {

    private Long idClient;
    private String codeClient;
    private String nomComplet;
    private String email;
    private String telephone;
    private String statut;
    private LocalDate dateNaissance;
    private String adresse;
    private String profession;
    private String typePieceIdentite;
    private String numeroPieceIdentiteMasque;
    private LocalDate dateExpirationPieceIdentite;
    private String photoIdentiteUrl;
    private String justificatifDomicileUrl;
    private String justificatifRevenusUrl;
    private String paysNationalite;
    private String paysResidence;
    private Boolean pep;
    private String niveauRisque;
    private String statutKyc;
    private Boolean kycComplet;
}
