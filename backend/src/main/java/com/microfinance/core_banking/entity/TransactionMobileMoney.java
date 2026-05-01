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
@Table(name = "transaction_mobile_money")
public class TransactionMobileMoney extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction_mobile_money")
    private Long idTransactionMobileMoney;

    @Column(name = "reference_transaction", nullable = false, unique = true, length = 80)
    private String referenceTransaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_wallet_client", nullable = false)
    private WalletClient walletClient;

    @Column(name = "type_transaction", nullable = false, length = 30)
    private String typeTransaction;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal frais = BigDecimal.ZERO;

    @Column(nullable = false, length = 30)
    private String statut = "TERMINEE";

    @Column(name = "reference_transaction_interne", length = 80)
    private String referenceTransactionInterne;
}

