package com.soutra.microfinance.dto.response.compte;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompteResponseDTO {

    /** Numero masque pour l'affichage (ex: CPT-****9-04) */
    private String numCompte;
    /** Numero complet necessaire pour les operations (depot, retrait, etc.) */
    private String numCompteComplet;
    private String typeCompte;
    private BigDecimal solde;
    private String devise;
    private BigDecimal decouvertAutorise;
    private String statut;
    private String clientNom;
    private String clientTelephone;
    private String clientEmail;
    private String clientNumero;
    private String clientStatut;
    private String agenceNom;
    private String agenceCode;
    private String agenceAdresse;
    private java.time.LocalDate dateOuverture;
    private java.time.LocalDateTime dateDerniereOp;
}

