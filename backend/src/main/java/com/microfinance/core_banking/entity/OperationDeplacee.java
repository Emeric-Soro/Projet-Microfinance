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
@Table(name = "operation_deplacee")
public class OperationDeplacee extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_operation_deplacee")
    private Long idOperationDeplacee;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_transaction", nullable = false)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_agence_origine", nullable = false)
    private Agence agenceOrigine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_agence_operante", nullable = false)
    private Agence agenceOperante;

    @Column(name = "type_operation", nullable = false, length = 40)
    private String typeOperation;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false, length = 10)
    private String devise = "XOF";

    @Column(nullable = false, length = 30)
    private String statut = "INITIEE";

    @Column(name = "reference_operation", nullable = false, unique = true, length = 80)
    private String referenceOperation;

    @Column(name = "date_operation", nullable = false)
    private LocalDateTime dateOperation;

    @Column(length = 500)
    private String commentaire;
}

