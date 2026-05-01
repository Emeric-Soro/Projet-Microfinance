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
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "remboursement_credit")
public class RemboursementCredit extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_remboursement_credit")
    private Long idRemboursementCredit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_credit", nullable = false)
    private Credit credit;

    @Column(name = "reference_remboursement", nullable = false, unique = true, length = 80)
    private String referenceRemboursement;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(name = "capital_paye", nullable = false, precision = 19, scale = 2)
    private BigDecimal capitalPaye = BigDecimal.ZERO;

    @Column(name = "interet_paye", nullable = false, precision = 19, scale = 2)
    private BigDecimal interetPaye = BigDecimal.ZERO;

    @Column(name = "assurance_payee", nullable = false, precision = 19, scale = 2)
    private BigDecimal assurancePayee = BigDecimal.ZERO;

    @Column(name = "reference_transaction", nullable = false, unique = true, length = 80)
    private String referenceTransaction;

    @Column(name = "date_paiement", nullable = false)
    private LocalDateTime datePaiement;

    @Column(nullable = false, length = 30)
    private String statut = "COMPTABILISE";
}
