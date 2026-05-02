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
@Table(name = "tontine")
public class Tontine extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tontine")
    private Long idTontine;

    @Column(name = "code_tontine", nullable = false, unique = true, length = 40)
    private String codeTontine;

    @Column(nullable = false, length = 150)
    private String intitule;

    @Column(name = "type_tontine", nullable = false, length = 20)
    private String typeTontine;

    @Column(name = "montant_cotisation", precision = 19, scale = 2)
    private BigDecimal montantCotisation;

    @Column(length = 20)
    private String periodicite;

    @Column(name = "nombre_participants")
    private Integer nombreParticipants;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(nullable = false, length = 20)
    private String statut = "ACTIVE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agence")
    private Agence agence;
}
