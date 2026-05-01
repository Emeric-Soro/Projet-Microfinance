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
@Table(name = "ordre_paiement_externe")
public class OrdrePaiementExterne extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ordre_paiement_externe")
    private Long idOrdrePaiementExterne;

    @Column(name = "reference_ordre", nullable = false, unique = true, length = 80)
    private String referenceOrdre;

    @Column(name = "type_flux", nullable = false, length = 40)
    private String typeFlux;

    @Column(nullable = false, length = 20)
    private String sens;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal frais = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_compte", nullable = false)
    private Compte compte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lot_compensation")
    private LotCompensation lotCompensation;

    @Column(name = "reference_externe", length = 80)
    private String referenceExterne;

    @Column(name = "reference_transaction_interne", length = 80)
    private String referenceTransactionInterne;

    @Column(name = "destination_detail", length = 150)
    private String destinationDetail;

    @Column(name = "date_initiation", nullable = false)
    private LocalDateTime dateInitiation;

    @Column(name = "date_reglement")
    private LocalDateTime dateReglement;

    @Column(name = "date_rapprochement")
    private LocalDateTime dateRapprochement;

    @Column(nullable = false, length = 30)
    private String statut = "INITIE";
}
