package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "liste_pep")
public class ListePep extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pep")
    private Long idPep;

    @Column(name = "nom_complet", nullable = false, length = 200)
    private String nomComplet;

    @Column(length = 200)
    private String fonction;

    @Column(length = 100)
    private String pays;

    @Column(name = "niveau_risque", length = 20)
    private String niveauRisque;

    @Column(name = "date_debut_mandat")
    private LocalDate dateDebutMandat;

    @Column(name = "date_fin_mandat")
    private LocalDate dateFinMandat;

    @Column(nullable = false)
    private Boolean actif = true;
}
