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
@Table(name = "parametre_agence")
public class ParametreAgence extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametre_agence")
    private Long idParametreAgence;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_agence", nullable = false)
    private Agence agence;

    @Column(name = "code_parametre", nullable = false, length = 80)
    private String codeParametre;

    @Column(name = "valeur_parametre", nullable = false, length = 500)
    private String valeurParametre;

    @Column(name = "type_valeur", nullable = false, length = 30)
    private String typeValeur = "STRING";

    @Column(name = "description_parametre", length = 255)
    private String descriptionParametre;

    @Column(name = "date_effet", nullable = false)
    private LocalDate dateEffet;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(nullable = false)
    private Boolean actif = Boolean.TRUE;

    @Column(name = "version_parametre", nullable = false)
    private Integer versionParametre = 1;
}

