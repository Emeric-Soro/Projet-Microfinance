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
@Table(name = "produit_credit")
public class ProduitCredit extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit_credit")
    private Long idProduitCredit;

    @Column(name = "code_produit", nullable = false, unique = true, length = 40)
    private String codeProduit;

    @Column(nullable = false, length = 150)
    private String libelle;

    @Column(nullable = false, length = 80)
    private String categorie;

    @Column(name = "taux_annuel", nullable = false, precision = 9, scale = 4)
    private BigDecimal tauxAnnuel;

    @Column(name = "duree_min_mois", nullable = false)
    private Integer dureeMinMois;

    @Column(name = "duree_max_mois", nullable = false)
    private Integer dureeMaxMois;

    @Column(name = "montant_min", nullable = false, precision = 19, scale = 2)
    private BigDecimal montantMin;

    @Column(name = "montant_max", nullable = false, precision = 19, scale = 2)
    private BigDecimal montantMax;

    @Column(name = "frais_dossier", nullable = false, precision = 19, scale = 2)
    private BigDecimal fraisDossier = BigDecimal.ZERO;

    @Column(name = "assurance_taux", nullable = false, precision = 9, scale = 4)
    private BigDecimal assuranceTaux = BigDecimal.ZERO;

    @Column(nullable = false, length = 30)
    private String statut = "ACTIF";
}

