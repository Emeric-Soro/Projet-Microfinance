package com.soutra.microfinance.entity.comptabilite;

import com.soutra.microfinance.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "soutra_ecriture_comptable")
// Ligne d'ecriture comptable : mouvement debit ou credit sur un compte du grand livre.
public class EcritureComptable extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    // Identifiant auto-genere de la ligne d'ecriture.
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    // Transaction comptable parente.
    private TransactionComptable transaction;

    @ManyToOne(optional = false)
    @JoinColumn(name = "numero_compte", nullable = false)
    // Compte du grand livre impacte par cette ecriture.
    private CompteComptable compte;

    @Column(nullable = false, precision = 19, scale = 2)
    // Montant au debit (positif si mouvement debit, 0 sinon).
    private BigDecimal debit;

    @Column(nullable = false, precision = 19, scale = 2)
    // Montant au credit (positif si mouvement credit, 0 sinon).
    private BigDecimal credit;
}
