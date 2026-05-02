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
@Table(name = "tour_tontine")
public class TourTontine extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tour_tontine")
    private Long idTourTontine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tontine", nullable = false)
    private Tontine tontine;

    @Column(name = "numero_tour", nullable = false)
    private Integer numeroTour;

    @Column(name = "date_tour")
    private LocalDate dateTour;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_beneficiaire", nullable = false)
    private Client beneficiaire;

    @Column(name = "montant_collecte", precision = 19, scale = 2)
    private BigDecimal montantCollecte;

    @Column(nullable = false, length = 20)
    private String statut = "EN_ATTENTE";
}
