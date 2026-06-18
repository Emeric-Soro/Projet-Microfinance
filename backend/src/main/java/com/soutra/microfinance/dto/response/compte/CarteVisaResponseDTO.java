package com.soutra.microfinance.dto.response.compte;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarteVisaResponseDTO {

    private Long idCarte;
    private String numeroCarteMasque;
    private LocalDate dateExpiration;
    private String statut;
    private String numCompte;
    private String titulaire;
}
