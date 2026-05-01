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

@Getter
@Setter
@Entity
@Table(name = "incident_operationnel")
public class IncidentOperationnel extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_incident_operationnel")
    private Long idIncidentOperationnel;

    @Column(name = "reference_incident", nullable = false, unique = true, length = 80)
    private String referenceIncident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_risque")
    private Risque risque;

    @Column(name = "type_incident", nullable = false, length = 60)
    private String typeIncident;

    @Column(nullable = false, length = 30)
    private String gravite;

    @Column(nullable = false, length = 30)
    private String statut = "OUVERT";

    @Column(length = 500)
    private String description;
}

