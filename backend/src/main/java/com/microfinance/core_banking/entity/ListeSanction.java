package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "liste_sanctions")
public class ListeSanction extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sanction")
    private Long idSanction;

    @Column(name = "nom_complet", nullable = false, length = 200)
    private String nomComplet;

    @Column(name = "type_personne", nullable = false, length = 20)
    private String typePersonne;

    @Column(length = 100)
    private String nationalite;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "lieu_naissance", length = 200)
    private String lieuNaissance;

    @Column(name = "type_sanction", length = 50)
    private String typeSanction;

    @Column(name = "reference_officielle", length = 100)
    private String referenceOfficielle;

    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;

    @Column(nullable = false)
    private Boolean actif = true;
}
