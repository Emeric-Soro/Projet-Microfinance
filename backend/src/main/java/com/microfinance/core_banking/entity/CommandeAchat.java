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
@Table(name = "commande_achat")
public class CommandeAchat extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_commande_achat")
    private Long idCommandeAchat;

    @Column(name = "reference_commande", nullable = false, unique = true, length = 80)
    private String referenceCommande;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_fournisseur", nullable = false)
    private Fournisseur fournisseur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agence")
    private Agence agence;

    @Column(nullable = false, length = 150)
    private String objet;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montant = BigDecimal.ZERO;

    @Column(name = "date_commande", nullable = false)
    private LocalDate dateCommande;

    @Column(nullable = false, length = 30)
    private String statut = "INITIEE";
}
