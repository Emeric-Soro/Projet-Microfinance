package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "consentement_open_banking")
public class ConsentementOpenBanking extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consentement")
    private Long idConsentement;

    @Column(name = "ref_consentement", nullable = false, unique = true, length = 60)
    private String refConsentement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_partenaire_api", nullable = false)
    private PartenaireApi partenaireApi;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "type_consentement", nullable = false, length = 20)
    private String typeConsentement;

    @Column(length = 200)
    private String scope;

    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Column(nullable = false, length = 30)
    private String statut = "ACTIF";

    @Column(name = "access_token_hash", length = 255)
    private String accessTokenHash;

    @Column(name = "refresh_token_hash", length = 255)
    private String refreshTokenHash;
}
