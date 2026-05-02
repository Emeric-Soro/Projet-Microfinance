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

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "chequier")
public class Chequier extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chequier")
    private Long idChequier;

    @Column(name = "numero_chequier", nullable = false, unique = true, length = 40)
    private String numeroChequier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_compte", nullable = false)
    private Compte compte;

    @Column(name = "nombre_cheques")
    private Integer nombreCheques;

    @Column(name = "premier_numero", length = 30)
    private String premierNumero;

    @Column(name = "dernier_numero", length = 30)
    private String dernierNumero;

    @Column(nullable = false, length = 20)
    private String statut = "COMMANDE";

    @Column(name = "date_commande")
    private LocalDateTime dateCommande;

    @Column(name = "date_remise")
    private LocalDateTime dateRemise;

    @Column(name = "motif_opposition", length = 255)
    private String motifOpposition;
}
