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
@Table(name = "risque")
public class Risque extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_risque")
    private Long idRisque;

    @Column(name = "code_risque", nullable = false, unique = true, length = 30)
    private String codeRisque;

    @Column(nullable = false, length = 60)
    private String categorie;

    @Column(nullable = false, length = 150)
    private String libelle;

    @Column(nullable = false, length = 30)
    private String niveau;

    @Column(nullable = false, length = 30)
    private String statut = "OUVERT";
}

