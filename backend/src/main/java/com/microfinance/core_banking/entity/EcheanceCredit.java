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
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "echeance_credit")
public class EcheanceCredit extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_echeance_credit")
    private Long idEcheanceCredit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_credit", nullable = false)
    private Credit credit;

    @Column(name = "numero_echeance", nullable = false)
    private Integer numeroEcheance;

    @Column(name = "date_echeance", nullable = false)
    private LocalDate dateEcheance;

    @Column(name = "capital_prevu", nullable = false, precision = 19, scale = 2)
    private BigDecimal capitalPrevu;

    @Column(name = "interet_prevu", nullable = false, precision = 19, scale = 2)
    private BigDecimal interetPrevu;

    @Column(name = "assurance_prevue", nullable = false, precision = 19, scale = 2)
    private BigDecimal assurancePrevue = BigDecimal.ZERO;

    @Column(name = "total_prevu", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPrevu;

    @Column(name = "capital_paye", nullable = false, precision = 19, scale = 2)
    private BigDecimal capitalPaye = BigDecimal.ZERO;

    @Column(name = "interet_paye", nullable = false, precision = 19, scale = 2)
    private BigDecimal interetPaye = BigDecimal.ZERO;

    @Column(name = "assurance_payee", nullable = false, precision = 19, scale = 2)
    private BigDecimal assurancePayee = BigDecimal.ZERO;

    @Column(name = "date_derniere_regularisation")
    private LocalDate dateDerniereRegularisation;

    @Column(nullable = false, length = 30)
    private String statut = "A_ECHOIR";
}
