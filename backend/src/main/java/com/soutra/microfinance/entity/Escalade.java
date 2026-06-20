package com.soutra.microfinance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "soutra_escalade")
public class Escalade extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_escalade")
    private Long idEscalade;

    @Column(name = "reference", length = 50, unique = true)
    private String reference;

    @Column(name = "type_escalade", nullable = false, length = 50)
    private String typeEscalade;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, length = 10)
    private String niveau = "N1";

    @Column(nullable = false, length = 30)
    private String statut = "OUVERTE";

    @Column(name = "id_client")
    private Long idClient;

    @Column(name = "id_transaction")
    private Long idTransaction;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @Column(name = "cree_par", length = 100)
    private String creePar;

    @Column(name = "traite_par", length = 100)
    private String traitePar;

    @Column(length = 50)
    private String action;

    @Column(length = 500)
    private String commentaire;
}
