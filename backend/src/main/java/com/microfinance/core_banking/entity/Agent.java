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

@Getter
@Setter
@Entity
@Table(name = "agent")
public class Agent extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agent")
    private Long idAgent;

    @Column(name = "code_agent", nullable = false, unique = true, length = 40)
    private String codeAgent;

    @Column(name = "nom_agent", nullable = false, length = 150)
    private String nomAgent;

    @Column(length = 30)
    private String telephone;

    @Column(length = 255)
    private String adresse;

    @Column(name = "type_agent", nullable = false, length = 20)
    private String typeAgent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agence_rattachement")
    private Agence agenceRattachement;

    @Column(nullable = false, length = 20)
    private String statut = "ACTIF";

    @Column(name = "date_agrement")
    private LocalDate dateAgrement;

    @Column(name = "date_resiliation")
    private LocalDate dateResiliation;
}
