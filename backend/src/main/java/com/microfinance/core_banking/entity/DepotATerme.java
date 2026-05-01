package com.microfinance.core_banking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "depot_a_terme")
public class DepotATerme extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_depot_terme")
    private Long idDepotTerme;

    @Column(name = "reference_depot", nullable = false, unique = true, length = 60)
    private String referenceDepot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_produit_epargne", nullable = false)
    private ProduitEpargne produitEpargne;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compte_support")
    private Compte compteSupport;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(name = "duree_mois", nullable = false)
    private Integer dureeMois;

    @Column(name = "taux_applique", nullable = false, precision = 9, scale = 4)
    private BigDecimal tauxApplique;

    @Column(name = "interets_estimes", nullable = false, precision = 19, scale = 2)
    private BigDecimal interetsEstimes = BigDecimal.ZERO;

    @Column(nullable = false, length = 30)
    private String statut = "ACTIF";

    @Column(name = "date_souscription", nullable = false)
    private LocalDate dateSouscription;

    @Column(name = "date_echeance", nullable = false)
    private LocalDate dateEcheance;

    @Column(name = "renouvellement_auto", nullable = false)
    private Boolean renouvellementAuto = Boolean.FALSE;

    @Column(name = "reference_transaction_souscription", length = 80)
    private String referenceTransactionSouscription;
}
