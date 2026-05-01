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
@Table(name = "alerte_conformite")
public class AlerteConformite extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerte_conformite")
    private Long idAlerteConformite;

    @Column(name = "reference_alerte", nullable = false, unique = true, length = 60)
    private String referenceAlerte;

    @Column(name = "type_alerte", nullable = false, length = 80)
    private String typeAlerte;

    @Column(name = "niveau_risque", nullable = false, length = 30)
    private String niveauRisque;

    @Column(nullable = false, length = 30)
    private String statut = "OUVERTE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transaction")
    private Transaction transaction;

    @Column(nullable = false, length = 255)
    private String resume;

    @Lob
    private String details;

    @Column(name = "date_detection", nullable = false)
    private LocalDateTime dateDetection;
}

