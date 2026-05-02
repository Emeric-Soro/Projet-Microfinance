package com.microfinance.core_banking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "critere_scoring")
public class CritereScoring extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_critere_scoring")
    private Long idCritereScoring;

    @Column(name = "code_critere", nullable = false, unique = true, length = 40)
    private String codeCritere;

    @Column(length = 150)
    private String libelle;

    @Column(length = 20)
    private String categorie;

    @Column(name = "type_valeur", length = 20)
    private String typeValeur;

    @Column(precision = 5, scale = 2)
    private BigDecimal poids;

    @Column(nullable = false)
    private Boolean actif = Boolean.TRUE;
}
