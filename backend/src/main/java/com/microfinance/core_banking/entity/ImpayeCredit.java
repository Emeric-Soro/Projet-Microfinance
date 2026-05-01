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
@Table(name = "impaye_credit")
public class ImpayeCredit extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_impaye_credit")
    private Long idImpayeCredit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_credit", nullable = false)
    private Credit credit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_echeance_credit", nullable = false)
    private EcheanceCredit echeanceCredit;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(name = "jours_retard", nullable = false)
    private Integer joursRetard;

    @Column(name = "classe_risque", nullable = false, length = 30)
    private String classeRisque;

    @Column(nullable = false, length = 30)
    private String statut = "OUVERT";
}
