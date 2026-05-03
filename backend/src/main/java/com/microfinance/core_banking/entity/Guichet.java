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
@Table(name = "guichet")
public class Guichet extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_guichet")
    private Long idGuichet;

    @Column(name = "code_guichet", nullable = false, unique = true, length = 30)
    private String codeGuichet;

    @Column(name = "nom_guichet", nullable = false, length = 120)
    private String nomGuichet;

    @Column(nullable = false, length = 30)
    private String statut = "ACTIF";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_agence", nullable = false)
    private Agence agence;

    public String getLibelle() {
        return nomGuichet;
    }
}
