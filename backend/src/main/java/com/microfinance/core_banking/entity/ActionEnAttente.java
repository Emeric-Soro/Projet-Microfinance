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
@Table(name = "action_en_attente")
public class ActionEnAttente extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_action_en_attente")
    private Long idActionEnAttente;

    @Column(name = "type_action", nullable = false, length = 80)
    private String typeAction;

    @Column(nullable = false, length = 80)
    private String ressource;

    @Column(name = "reference_ressource", length = 80)
    private String referenceRessource;

    @Lob
    @Column(name = "ancienne_valeur")
    private String ancienneValeur;

    @Lob
    @Column(name = "nouvelle_valeur")
    private String nouvelleValeur;

    @Column(nullable = false, length = 30)
    private String statut = "EN_ATTENTE";

    @Column(name = "commentaire_maker", length = 500)
    private String commentaireMaker;

    @Column(name = "commentaire_checker", length = 500)
    private String commentaireChecker;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_maker", nullable = false)
    private Utilisateur maker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_checker")
    private Utilisateur checker;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;
}

