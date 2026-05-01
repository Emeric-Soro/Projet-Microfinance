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
@Table(name = "ligne_budget")
public class LigneBudget extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ligne_budget")
    private Long idLigneBudget;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_budget", nullable = false)
    private BudgetExploitation budget;

    @Column(nullable = false, length = 100)
    private String rubrique;

    @Column(name = "montant_prevu", nullable = false, precision = 19, scale = 2)
    private BigDecimal montantPrevu = BigDecimal.ZERO;

    @Column(name = "montant_engage", nullable = false, precision = 19, scale = 2)
    private BigDecimal montantEngage = BigDecimal.ZERO;

    @Column(name = "montant_consomme", nullable = false, precision = 19, scale = 2)
    private BigDecimal montantConsomme = BigDecimal.ZERO;
}
