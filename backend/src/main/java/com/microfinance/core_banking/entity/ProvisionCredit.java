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
@Table(name = "provision_credit")
public class ProvisionCredit extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_provision_credit")
    private Long idProvisionCredit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_credit", nullable = false)
    private Credit credit;

    @Column(name = "date_calcul", nullable = false)
    private LocalDate dateCalcul;

    @Column(name = "taux_provision", nullable = false, precision = 9, scale = 4)
    private BigDecimal tauxProvision = BigDecimal.ZERO;

    @Column(name = "montant_provision", nullable = false, precision = 19, scale = 2)
    private BigDecimal montantProvision = BigDecimal.ZERO;

    @Column(name = "reference_piece_comptable", length = 80)
    private String referencePieceComptable;

    @Column(nullable = false, length = 30)
    private String statut = "CALCULEE";
}
