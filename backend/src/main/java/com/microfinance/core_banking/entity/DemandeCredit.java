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
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "demande_credit")
public class DemandeCredit extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_demande_credit")
    private Long idDemandeCredit;

    @Column(name = "reference_dossier", nullable = false, unique = true, length = 60)
    private String referenceDossier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_produit_credit", nullable = false)
    private ProduitCredit produitCredit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agence")
    private Agence agence;

    @Column(name = "montant_demande", nullable = false, precision = 19, scale = 2)
    private BigDecimal montantDemande;

    @Column(name = "duree_mois", nullable = false)
    private Integer dureeMois;

    @Column(name = "objet_credit", length = 255)
    private String objetCredit;

    @Column(nullable = false, length = 30)
    private String statut = "BROUILLON";

    @Column(name = "score_credit")
    private Integer scoreCredit;

    @Column(name = "avis_comite", length = 500)
    private String avisComite;

    @Column(name = "decision_finale", length = 500)
    private String decisionFinale;

    @Column(name = "date_decision")
    private LocalDateTime dateDecision;
}

