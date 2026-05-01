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
@Table(name = "classe_comptable")
public class ClasseComptable extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_classe_comptable")
    private Long idClasseComptable;

    @Column(name = "code_classe", nullable = false, unique = true, length = 10)
    private String codeClasse;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(name = "ordre_affichage", nullable = false)
    private Integer ordreAffichage = 0;
}

