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
@Table(name = "caisse")
public class Caisse extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caisse")
    private Long idCaisse;

    @Column(name = "code_caisse", nullable = false, unique = true, length = 30)
    private String codeCaisse;

    @Column(nullable = false, length = 120)
    private String libelle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_agence", nullable = false)
    private Agence agence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_guichet")
    private Guichet guichet;

    @Column(nullable = false, length = 30)
    private String statut = "ACTIVE";

    @Column(name = "solde_theorique", nullable = false, precision = 19, scale = 2)
    private BigDecimal soldeTheorique = BigDecimal.ZERO;
}

