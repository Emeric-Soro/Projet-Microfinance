package com.soutra.microfinance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "soutra_derogation")
public class Derogation extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_derogation")
    private Long idDerogation;

    @Column(name = "reference", length = 50, unique = true)
    private String reference;

    @Column(name = "type_derogation", nullable = false, length = 50)
    private String typeDerogation;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, length = 500)
    private String motif;

    @Column(name = "montant_concerne", precision = 15, scale = 2)
    private BigDecimal montantConcerne;

    @Column(name = "id_client")
    private Long idClient;

    @Column(name = "id_transaction")
    private Long idTransaction;

    @Column(nullable = false, length = 30)
    private String statut = "SOUMISE";

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @Column(name = "cree_par", length = 100)
    private String creePar;

    @Column(name = "traite_par", length = 100)
    private String traitePar;

    @Column(name = "motif_traitement", length = 500)
    private String motifTraitement;
}
