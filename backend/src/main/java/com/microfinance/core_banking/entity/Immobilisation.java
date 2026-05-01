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
@Table(name = "immobilisation")
public class Immobilisation extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_immobilisation")
    private Long idImmobilisation;

    @Column(name = "code_immobilisation", nullable = false, unique = true, length = 40)
    private String codeImmobilisation;

    @Column(nullable = false, length = 150)
    private String libelle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agence")
    private Agence agence;

    @Column(name = "valeur_origine", nullable = false, precision = 19, scale = 2)
    private BigDecimal valeurOrigine = BigDecimal.ZERO;

    @Column(name = "duree_amortissement_mois", nullable = false)
    private Integer dureeAmortissementMois;

    @Column(name = "amortissement_mensuel", nullable = false, precision = 19, scale = 2)
    private BigDecimal amortissementMensuel = BigDecimal.ZERO;

    @Column(name = "valeur_nette", nullable = false, precision = 19, scale = 2)
    private BigDecimal valeurNette = BigDecimal.ZERO;

    @Column(name = "date_acquisition", nullable = false)
    private LocalDate dateAcquisition;

    @Column(nullable = false, length = 30)
    private String statut = "ACTIVE";
}
