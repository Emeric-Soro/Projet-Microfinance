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
@Table(name = "employe")
public class Employe extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_employe")
    private Long idEmploye;

    @Column(nullable = false, unique = true, length = 40)
    private String matricule;

    @Column(name = "nom_complet", nullable = false, length = 150)
    private String nomComplet;

    @Column(length = 120)
    private String poste;

    @Column(length = 120)
    private String service;

    @Column(nullable = false, length = 30)
    private String statut = "ACTIF";

    @Column(name = "date_embauche")
    private LocalDate dateEmbauche;

    @Column(length = 150)
    private String email;

    @Column(length = 30)
    private String telephone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agence")
    private Agence agence;
}

