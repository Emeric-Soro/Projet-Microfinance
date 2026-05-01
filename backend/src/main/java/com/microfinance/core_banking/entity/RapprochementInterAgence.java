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
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "rapprochement_inter_agence")
public class RapprochementInterAgence extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rapprochement_inter_agence")
    private Long idRapprochementInterAgence;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_agence_source", nullable = false)
    private Agence agenceSource;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_agence_destination", nullable = false)
    private Agence agenceDestination;

    @Column(name = "periode_debut", nullable = false)
    private LocalDate periodeDebut;

    @Column(name = "periode_fin", nullable = false)
    private LocalDate periodeFin;

    @Column(name = "date_rapprochement", nullable = false)
    private LocalDateTime dateRapprochement;

    @Column(name = "montant_debit", nullable = false, precision = 19, scale = 2)
    private BigDecimal montantDebit = BigDecimal.ZERO;

    @Column(name = "montant_credit", nullable = false, precision = 19, scale = 2)
    private BigDecimal montantCredit = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal ecart = BigDecimal.ZERO;

    @Column(nullable = false, length = 30)
    private String statut = "BROUILLON";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_validateur")
    private Utilisateur validateur;

    @Column(length = 500)
    private String commentaire;
}

