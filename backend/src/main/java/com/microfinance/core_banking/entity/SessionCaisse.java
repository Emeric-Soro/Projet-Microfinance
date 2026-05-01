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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "session_caisse")
public class SessionCaisse extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_session_caisse")
    private Long idSessionCaisse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_caisse", nullable = false)
    private Caisse caisse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "date_ouverture", nullable = false)
    private LocalDateTime dateOuverture;

    @Column(name = "date_fermeture")
    private LocalDateTime dateFermeture;

    @Column(name = "solde_ouverture", nullable = false, precision = 19, scale = 2)
    private BigDecimal soldeOuverture;

    @Column(name = "solde_theorique_fermeture", nullable = false, precision = 19, scale = 2)
    private BigDecimal soldeTheoriqueFermeture = BigDecimal.ZERO;

    @Column(name = "solde_physique_fermeture", precision = 19, scale = 2)
    private BigDecimal soldePhysiqueFermeture;

    @Column(precision = 19, scale = 2)
    private BigDecimal ecart;

    @Column(nullable = false, length = 30)
    private String statut = "OUVERTE";

    @Column(length = 500)
    private String commentaire;
}

