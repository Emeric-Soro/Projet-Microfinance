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
@Table(name = "commission_agent")
public class CommissionAgent extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_commission_agent")
    private Long idCommissionAgent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_agent", nullable = false)
    private Agent agent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_transaction_agent", nullable = false)
    private TransactionAgent transactionAgent;

    @Column(name = "type_commission", nullable = false, length = 20)
    private String typeCommission;

    @Column(name = "montant_commission", precision = 19, scale = 2)
    private BigDecimal montantCommission;

    @Column(name = "date_calcul")
    private LocalDateTime dateCalcul;

    @Column(name = "date_paiement")
    private LocalDateTime datePaiement;

    @Column(nullable = false, length = 20)
    private String statut = "CALCULEE";
}
