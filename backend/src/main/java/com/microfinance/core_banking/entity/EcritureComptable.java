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

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "ecriture_comptable")
public class EcritureComptable extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ecriture_comptable")
    private Long idEcritureComptable;

    @Column(name = "reference_piece", nullable = false, unique = true, length = 80)
    private String referencePiece;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_journal_comptable", nullable = false)
    private JournalComptable journalComptable;

    @Column(name = "date_comptable", nullable = false)
    private LocalDate dateComptable;

    @Column(name = "date_valeur")
    private LocalDate dateValeur;

    @Column(nullable = false, length = 255)
    private String libelle;

    @Column(name = "source_type", length = 40)
    private String sourceType;

    @Column(name = "source_reference", length = 80)
    private String sourceReference;

    @Column(nullable = false, length = 30)
    private String statut = "COMPTABILISEE";
}

