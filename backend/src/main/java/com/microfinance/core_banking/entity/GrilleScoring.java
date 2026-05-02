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
@Table(name = "grille_scoring")
public class GrilleScoring extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_grille_scoring")
    private Long idGrilleScoring;

    @Column(name = "code_grille", nullable = false, unique = true, length = 40)
    private String codeGrille;

    @Column(length = 150)
    private String libelle;

    @Column(name = "seuil_approbation")
    private Integer seuilApprobation;

    @Column(name = "seuil_rejet")
    private Integer seuilRejet;

    @Column(nullable = false)
    private Boolean actif = Boolean.TRUE;
}
