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

@Getter
@Setter
@Entity
@Table(name = "ligne_ecriture_comptable")
public class LigneEcritureComptable extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ligne_ecriture_comptable")
    private Long idLigneEcritureComptable;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ecriture_comptable", nullable = false)
    private EcritureComptable ecritureComptable;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_compte_comptable", nullable = false)
    private CompteComptable compteComptable;

    @Column(nullable = false, length = 20)
    private String sens;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(name = "reference_auxiliaire", length = 80)
    private String referenceAuxiliaire;

    @Column(name = "libelle_auxiliaire", length = 150)
    private String libelleAuxiliaire;
}

