package com.microfinance.core_banking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "fournisseur")
public class Fournisseur extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fournisseur")
    private Long idFournisseur;

    @Column(name = "code_fournisseur", nullable = false, unique = true, length = 40)
    private String codeFournisseur;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(length = 150)
    private String contact;

    @Column(length = 30)
    private String telephone;

    @Column(length = 150)
    private String email;

    @Column(nullable = false, length = 30)
    private String statut = "ACTIF";
}
