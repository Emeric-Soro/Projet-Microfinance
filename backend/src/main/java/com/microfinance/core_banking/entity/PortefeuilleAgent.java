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
@Table(name = "portefeuille_agent")
public class PortefeuilleAgent extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_portefeuille_agent")
    private Long idPortefeuilleAgent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_agent", nullable = false)
    private Agent agent;

    @Column(precision = 19, scale = 2)
    private BigDecimal solde = BigDecimal.ZERO;

    @Column(name = "plafond_maximum", precision = 19, scale = 2)
    private BigDecimal plafondMaximum;

    @Column(name = "plafond_minimum", precision = 19, scale = 2)
    private BigDecimal plafondMinimum;

    @Column(nullable = false, length = 10)
    private String devise = "XOF";

    @Column(nullable = false, length = 20)
    private String statut = "ACTIF";
}
