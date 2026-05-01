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

@Getter
@Setter
@Entity
@Table(name = "compte_liaison_agence")
public class CompteLiaisonAgence extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compte_liaison_agence")
    private Long idCompteLiaisonAgence;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_agence_source", nullable = false)
    private Agence agenceSource;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_agence_destination", nullable = false)
    private Agence agenceDestination;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_compte_comptable", nullable = false)
    private CompteComptable compteComptable;

    @Column(nullable = false, length = 150)
    private String libelle;

    @Column(nullable = false)
    private Boolean actif = Boolean.TRUE;
}

