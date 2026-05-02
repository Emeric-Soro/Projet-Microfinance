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
@Table(name = "transaction_agent")
public class TransactionAgent extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction_agent")
    private Long idTransactionAgent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_agent", nullable = false)
    private Agent agent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @Column(name = "type_operation", nullable = false, length = 30)
    private String typeOperation;

    @Column(precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(precision = 19, scale = 2)
    private BigDecimal frais = BigDecimal.ZERO;

    @Column(name = "commission_agent", precision = 19, scale = 2)
    private BigDecimal commissionAgent = BigDecimal.ZERO;

    @Column(name = "reference_transaction", length = 100)
    private String referenceTransaction;

    @Column(name = "date_transaction")
    private LocalDateTime dateTransaction;

    @Column(nullable = false, length = 20)
    private String statut = "EN_ATTENTE";
}
