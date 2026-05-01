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
@Table(name = "agence")
public class Agence extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agence")
    private Long idAgence;

    @Column(name = "code_agence", nullable = false, unique = true, length = 30)
    private String codeAgence;

    @Column(name = "nom_agence", nullable = false, length = 150)
    private String nomAgence;

    @Column(length = 255)
    private String adresse;

    @Column(length = 30)
    private String telephone;

    @Column(nullable = false, length = 30)
    private String statut = "ACTIVE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_region")
    private Region region;
}

