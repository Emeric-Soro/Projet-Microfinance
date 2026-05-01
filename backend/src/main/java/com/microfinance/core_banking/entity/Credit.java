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
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "credit")
public class Credit extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_credit")
    private Long idCredit;

    @Column(name = "reference_credit", nullable = false, unique = true, length = 60)
    private String referenceCredit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_demande_credit", nullable = false)
    private DemandeCredit demandeCredit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @Column(name = "montant_accorde", nullable = false, precision = 19, scale = 2)
    private BigDecimal montantAccorde;

    @Column(name = "taux_annuel", nullable = false, precision = 9, scale = 4)
    private BigDecimal tauxAnnuel;

    @Column(name = "duree_mois", nullable = false)
    private Integer dureeMois;

    @Column(precision = 19, scale = 2)
    private BigDecimal mensualite;

    @Column(name = "capital_restant_du", nullable = false, precision = 19, scale = 2)
    private BigDecimal capitalRestantDu;

    @Column(name = "frais_preleves", nullable = false, precision = 19, scale = 2)
    private BigDecimal fraisPreleves = BigDecimal.ZERO;

    @Column(nullable = false, length = 30)
    private String statut = "ACTIF";

    @Column(name = "date_deblocage")
    private LocalDateTime dateDeblocage;

    @Column(name = "date_prochaine_echeance")
    private LocalDate dateProchaineEcheance;

    @Column(name = "reference_transaction_deblocage", length = 80)
    private String referenceTransactionDeblocage;
}
