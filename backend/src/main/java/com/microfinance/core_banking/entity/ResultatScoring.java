package com.microfinance.core_banking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "resultat_scoring")
public class ResultatScoring extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resultat_scoring")
    private Long idResultatScoring;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_demande_credit", nullable = false)
    private DemandeCredit demandeCredit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_grille_scoring", nullable = false)
    private GrilleScoring grilleScoring;

    @Column(name = "score_total")
    private Integer scoreTotal;

    @Column(length = 20)
    private String decision;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String details;

    @Column(name = "date_scoring")
    private LocalDateTime dateScoring;
}
