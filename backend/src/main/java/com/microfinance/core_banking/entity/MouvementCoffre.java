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
@Table(name = "mouvement_coffre")
public class MouvementCoffre extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mouvement_coffre")
    private Long idMouvementCoffre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_coffre", nullable = false)
    private Coffre coffre;

    @Column(name = "type_mouvement", nullable = false, length = 40)
    private String typeMouvement;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(name = "reference_mouvement", nullable = false, unique = true, length = 80)
    private String referenceMouvement;

    @Column(length = 500)
    private String commentaire;
}

