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
@Table(name = "delestage_caisse")
public class DelestageCaisse extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_delestage_caisse")
    private Long idDelestageCaisse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_coffre", nullable = false)
    private Coffre coffre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_caisse", nullable = false)
    private Caisse caisse;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(name = "reference_operation", nullable = false, unique = true, length = 80)
    private String referenceOperation;
}

