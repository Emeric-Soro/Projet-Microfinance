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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "mutation_personnel")
public class MutationPersonnel extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mutation_personnel")
    private Long idMutationPersonnel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_employe", nullable = false)
    private Employe employe;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_agence_source", nullable = false)
    private Agence agenceSource;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_agence_destination", nullable = false)
    private Agence agenceDestination;

    @Column(name = "date_mutation", nullable = false)
    private LocalDate dateMutation;

    @Column(length = 500)
    private String motif;

    @Column(nullable = false, length = 30)
    private String statut = "INITIEE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_validateur")
    private Utilisateur validateur;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "commentaire_validation", length = 500)
    private String commentaireValidation;
}

