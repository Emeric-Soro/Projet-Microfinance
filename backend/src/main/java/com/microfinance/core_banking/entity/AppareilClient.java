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

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "appareil_client")
public class AppareilClient extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_appareil_client")
    private Long idAppareilClient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @Column(name = "empreinte_appareil", nullable = false, unique = true, length = 120)
    private String empreinteAppareil;

    @Column(nullable = false, length = 40)
    private String plateforme;

    @Column(name = "nom_appareil", length = 120)
    private String nomAppareil;

    @Column(nullable = false)
    private Boolean autorise = Boolean.TRUE;

    @Column(name = "derniere_connexion")
    private LocalDateTime derniereConnexion;
}

