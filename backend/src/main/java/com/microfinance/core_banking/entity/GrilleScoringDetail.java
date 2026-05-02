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

@Getter
@Setter
@Entity
@Table(name = "grille_scoring_detail")
public class GrilleScoringDetail extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_grille_scoring_detail")
    private Long idGrilleScoringDetail;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_grille_scoring", nullable = false)
    private GrilleScoring grilleScoring;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_critere_scoring", nullable = false)
    private CritereScoring critereScoring;

    @Column(name = "valeur_min", length = 100)
    private String valeurMin;

    @Column(name = "valeur_max", length = 100)
    private String valeurMax;

    @Column(nullable = false)
    private Integer points;
}
