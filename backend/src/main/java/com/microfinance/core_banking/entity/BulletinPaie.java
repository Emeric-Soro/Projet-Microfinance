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
@Table(name = "bulletin_paie")
public class BulletinPaie extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bulletin_paie")
    private Long idBulletinPaie;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_employe", nullable = false)
    private Employe employe;

    @Column(nullable = false, length = 20)
    private String periode;

    @Column(name = "salaire_brut", nullable = false, precision = 19, scale = 2)
    private BigDecimal salaireBrut = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal retenues = BigDecimal.ZERO;

    @Column(name = "salaire_net", nullable = false, precision = 19, scale = 2)
    private BigDecimal salaireNet = BigDecimal.ZERO;

    @Column(nullable = false, length = 30)
    private String statut = "BROUILLON";
}
