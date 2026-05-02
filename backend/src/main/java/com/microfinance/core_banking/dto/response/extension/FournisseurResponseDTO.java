package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

@Data
public class FournisseurResponseDTO {
    private Long idFournisseur;
    private String codeFournisseur;
    private String nom;
    private String contact;
    private String telephone;
    private String email;
    private String statut;
}
