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
@Table(name = "partenaire_api")
public class PartenaireApi extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_partenaire_api")
    private Long idPartenaireApi;

    @Column(name = "code_partenaire", nullable = false, unique = true, length = 40)
    private String codePartenaire;

    @Column(name = "nom_partenaire", nullable = false, length = 150)
    private String nomPartenaire;

    @Column(name = "type_partenaire", nullable = false, length = 80)
    private String typePartenaire;

    @Column(name = "webhook_url", length = 255)
    private String webhookUrl;

    @Column(nullable = false, length = 30)
    private String statut = "ACTIF";

    @Column(name = "oauth_client_id", length = 120)
    private String oauthClientId;

    @Column(name = "cle_api", length = 255)
    private String cleApi;

    @Column(name = "quotas_journaliers", nullable = false)
    private Integer quotasJournaliers = 0;
}

