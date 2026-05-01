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
@Table(name = "compte_comptable")
public class CompteComptable extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compte_comptable")
    private Long idCompteComptable;

    @Column(name = "numero_compte", nullable = false, unique = true, length = 30)
    private String numeroCompte;

    @Column(nullable = false, length = 150)
    private String intitule;

    @Column(name = "type_solde", nullable = false, length = 20)
    private String typeSolde = "MIXTE";

    @Column(name = "compte_interne", nullable = false)
    private Boolean compteInterne = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_classe_comptable")
    private ClasseComptable classeComptable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agence")
    private Agence agence;
}

