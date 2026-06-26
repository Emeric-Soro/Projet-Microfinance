package com.soutra.microfinance.dto.response.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO réservé aux administrateurs contenant les données sensibles du client en clair.
 * Ce DTO ne doit jamais être exposé à des rôles non administrateurs.
 * Tout accès à cet endpoint est tracé dans le journal d'audit.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientConfidentielResponseDTO {

    private Long idClient;
    private String codeClient;
    private String nom;
    private String prenom;
    private String nomComplet;
    private String email;
    private String telephone;
    private String statut;
    private LocalDate dateNaissance;
    private String adresse;
    private String profession;
    private String secteurActivite;
    private BigDecimal revenuMensuel;

    // Données sensibles en clair (réservées aux admins)
    private String typePieceIdentite;
    private String numeroPieceIdentite;          // Valeur complète, non masquée
    private String numeroPieceIdentiteMasque;    // Version masquée pour référence
    private LocalDate dateExpirationPieceIdentite;

    private String photoIdentiteUrl;
    private String photoProfilUrl;
    private String justificatifDomicileUrl;
    private String justificatifRevenusUrl;
    private String paysNationalite;
    private String paysResidence;
    private Boolean pep;
    private String niveauRisque;
    private String statutKyc;
    private Boolean kycComplet;
    private LocalDate dateInscription;
}
