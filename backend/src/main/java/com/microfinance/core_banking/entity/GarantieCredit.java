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

@Getter
@Setter
@Entity
@Table(name = "garantie_credit")
public class GarantieCredit extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_garantie_credit")
    private Long idGarantieCredit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_credit", nullable = false)
    private Credit credit;

    @Column(name = "type_garantie", nullable = false, length = 60)
    private String typeGarantie;

    @Column(nullable = false, length = 150)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valeur;

    @Column(name = "valeur_nantie", nullable = false, precision = 19, scale = 2)
    private BigDecimal valeurNantie = BigDecimal.ZERO;

    @Column(nullable = false, length = 30)
    private String statut = "ACTIVE";
}
