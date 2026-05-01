package com.microfinance.core_banking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "produit_epargne")
public class ProduitEpargne extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit_epargne")
    private Long idProduitEpargne;

    @Column(name = "code_produit", nullable = false, unique = true, length = 40)
    private String codeProduit;

    @Column(nullable = false, length = 150)
    private String libelle;

    @Column(nullable = false, length = 80)
    private String categorie;

    @Column(name = "taux_interet", nullable = false, precision = 9, scale = 4)
    private BigDecimal tauxInteret = BigDecimal.ZERO;

    @Column(name = "depot_initial_min", nullable = false, precision = 19, scale = 2)
    private BigDecimal depotInitialMin = BigDecimal.ZERO;

    @Column(name = "solde_minimum", nullable = false, precision = 19, scale = 2)
    private BigDecimal soldeMinimum = BigDecimal.ZERO;

    @Column(name = "frequence_interet", length = 40)
    private String frequenceInteret;

    @Column(nullable = false, length = 30)
    private String statut = "ACTIF";
}

