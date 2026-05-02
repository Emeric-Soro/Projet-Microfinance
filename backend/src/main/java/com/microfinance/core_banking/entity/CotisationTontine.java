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
@Table(name = "cotisation_tontine")
public class CotisationTontine extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cotisation_tontine")
    private Long idCotisationTontine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tour_tontine", nullable = false)
    private TourTontine tourTontine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_participant", nullable = false)
    private Client participant;

    @Column(name = "montant_cotise", precision = 19, scale = 2)
    private BigDecimal montantCotise;

    @Column(name = "date_cotisation")
    private LocalDate dateCotisation;

    @Column(name = "reference_transaction", length = 100)
    private String referenceTransaction;

    @Column(nullable = false, length = 20)
    private String statut = "IMPAYEE";
}
