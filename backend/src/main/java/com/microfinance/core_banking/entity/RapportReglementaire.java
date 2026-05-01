package com.microfinance.core_banking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "rapport_reglementaire")
public class RapportReglementaire extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rapport_reglementaire")
    private Long idRapportReglementaire;

    @Column(name = "code_rapport", nullable = false, unique = true, length = 40)
    private String codeRapport;

    @Column(name = "type_rapport", nullable = false, length = 80)
    private String typeRapport;

    @Column(nullable = false, length = 30)
    private String periode;

    @Column(nullable = false, length = 30)
    private String statut = "BROUILLON";

    @Column(name = "chemin_fichier", length = 255)
    private String cheminFichier;

    @Column(name = "date_generation")
    private LocalDateTime dateGeneration;

    @Column(length = 500)
    private String commentaire;
}
