package com.microfinance.core_banking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "commission_inter_agence")
public class CommissionInterAgence extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_commission_inter_agence")
    private Long idCommissionInterAgence;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_operation_deplacee", nullable = false)
    private OperationDeplacee operationDeplacee;

    @Column(name = "taux_commission", nullable = false, precision = 9, scale = 4)
    private BigDecimal tauxCommission = BigDecimal.ZERO;

    @Column(name = "montant_commission", nullable = false, precision = 19, scale = 2)
    private BigDecimal montantCommission = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compte_comptable")
    private CompteComptable compteComptable;

    @Column(nullable = false, length = 30)
    private String statut = "CALCULEE";

    @Column(name = "reference_piece", length = 80)
    private String referencePiece;

    @Column(name = "date_calcul", nullable = false)
    private LocalDateTime dateCalcul;

    @Column(name = "date_comptabilisation")
    private LocalDateTime dateComptabilisation;
}

