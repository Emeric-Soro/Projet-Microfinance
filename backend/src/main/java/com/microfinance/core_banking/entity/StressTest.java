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
@Table(name = "stress_test")
public class StressTest extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_stress_test")
    private Long idStressTest;

    @Column(name = "code_scenario", nullable = false, unique = true, length = 40)
    private String codeScenario;

    @Column(nullable = false, length = 150)
    private String libelle;

    @Column(name = "taux_defaut", nullable = false, precision = 9, scale = 4)
    private BigDecimal tauxDefaut = BigDecimal.ZERO;

    @Column(name = "taux_retrait", nullable = false, precision = 9, scale = 4)
    private BigDecimal tauxRetrait = BigDecimal.ZERO;

    @Column(nullable = false, length = 30)
    private String statut = "ACTIF";
}

