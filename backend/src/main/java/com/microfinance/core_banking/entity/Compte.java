package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "compte")
// Entite representant un compte bancaire.
public class Compte extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compte")
    // Identifiant unique du compte.
    private Long idCompte;

    @Column(name = "num_compte", nullable = false, length = 50, unique = true)
    // Numero unique du compte.
    private String numCompte;

    @Column(nullable = false, precision = 19, scale = 2)
    // Solde courant du compte.
    private BigDecimal solde;

    @Column(name = "date_ouverture", nullable = false)
    // Date d'ouverture du compte.
    private LocalDate dateOuverture;

    @Column(nullable = false, length = 10)
    // Devise utilisee par le compte.
    private String devise;

    @Column(name = "taux_interet", precision = 7, scale = 4)
    // Taux d'interet applique au compte.
    private BigDecimal tauxInteret;

    @Column(name = "decouvert_autorise", precision = 19, scale = 2)
    // Montant maximal de decouvert autorise.
    private BigDecimal decouvertAutorise;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_client", nullable = false)
    // Client proprietaire du compte.
    private Client client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_type_compte", nullable = false)
    // Type associe a ce compte.
    private TypeCompte typeCompte;

    @OneToMany(mappedBy = "compte")
    // Historique des statuts du compte.
    private List<StatutCompte> statutsCompte = new ArrayList<>();

    @OneToMany(mappedBy = "compte")
    // Cartes liees a ce compte.
    private List<CarteVisa> cartesVisa = new ArrayList<>();

    @OneToMany(mappedBy = "compte")
    // Agios calcules sur ce compte.
    private List<Agio> agios = new ArrayList<>();

    @OneToMany(mappedBy = "compte")
    // Lignes d'ecriture impactant ce compte.
    private List<LigneEcriture> lignesEcriture = new ArrayList<>();


}
