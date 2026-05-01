package com.microfinance.core_banking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "region")
public class Region extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_region")
    private Long idRegion;

    @Column(name = "code_region", nullable = false, unique = true, length = 30)
    private String codeRegion;

    @Column(name = "nom_region", nullable = false, length = 120)
    private String nomRegion;

    @Column(nullable = false, length = 30)
    private String statut = "ACTIVE";
}

