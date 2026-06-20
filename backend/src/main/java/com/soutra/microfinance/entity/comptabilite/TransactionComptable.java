package com.soutra.microfinance.entity.comptabilite;

import com.soutra.microfinance.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "soutra_transaction_comptable")
// Ecriture comptable SYSCOHADA : entete d'une operation au grand livre general.
public class TransactionComptable extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    // Identifiant auto-genere de la transaction comptable.
    private Long id;

    @Column(name = "date_transaction", nullable = false)
    // Date et heure de la transaction comptable.
    private LocalDateTime dateTransaction;

    @Column(nullable = false)
    // Libelle descriptif de l'operation comptable.
    private String libelle;

    @Column(name = "reference_operateur", length = 50)
    // Reference de l'operateur ayant genere l'ecriture.
    private String referenceOperateur;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    // Lignes d'ecriture (debit/credit) composant cette transaction.
    private List<EcritureComptable> ecritures = new ArrayList<>();
}
