package com.microfinance.core_banking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "cloture_comptable")
public class ClotureComptable extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cloture_comptable")
    private Long idClotureComptable;

    @Column(name = "type_cloture", nullable = false, length = 30)
    private String typeCloture;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(nullable = false, length = 30)
    private String statut = "TERMINEE";

    @Column(length = 500)
    private String commentaire;

    @Column(name = "total_ecritures", nullable = false)
    private Integer totalEcritures = 0;
}

