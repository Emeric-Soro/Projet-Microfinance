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
@Table(name = "resultat_stress_test")
public class ResultatStressTest extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resultat_stress_test")
    private Long idResultatStressTest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_stress_test", nullable = false)
    private StressTest stressTest;

    @Column(name = "encours_credit", nullable = false, precision = 19, scale = 2)
    private BigDecimal encoursCredit = BigDecimal.ZERO;

    @Column(name = "pertes_projetees", nullable = false, precision = 19, scale = 2)
    private BigDecimal pertesProjetees = BigDecimal.ZERO;

    @Column(name = "retraits_projetes", nullable = false, precision = 19, scale = 2)
    private BigDecimal retraitsProjetes = BigDecimal.ZERO;

    @Column(name = "liquidite_nette", nullable = false, precision = 19, scale = 2)
    private BigDecimal liquiditeNette = BigDecimal.ZERO;

    @Column(name = "statut_resultat", nullable = false, length = 30)
    private String statutResultat = "STABLE";
}
