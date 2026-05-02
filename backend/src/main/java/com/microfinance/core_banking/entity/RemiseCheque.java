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
@Table(name = "remise_cheque")
public class RemiseCheque extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_remise_cheque")
    private Long idRemiseCheque;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_chequier", nullable = false)
    private Chequier chequier;

    @Column(name = "numero_cheque", length = 30)
    private String numeroCheque;

    @Column(precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(length = 150)
    private String tireur;

    @Column(name = "date_remise")
    private LocalDateTime dateRemise;

    @Column(name = "date_valeur")
    private LocalDate dateValeur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_compte_remise", nullable = false)
    private Compte compteRemise;

    @Column(nullable = false, length = 20)
    private String statut = "REMIS";

    @Column(name = "reference_transaction", length = 100)
    private String referenceTransaction;
}
