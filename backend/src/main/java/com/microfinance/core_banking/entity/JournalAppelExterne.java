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
@Table(name = "journal_appel_externe")
public class JournalAppelExterne extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_journal_appel")
    private Long idJournalAppel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_partenaire_api")
    private PartenaireApi partenaireApi;

    @Column(name = "code_partenaire", nullable = false, length = 40)
    private String codePartenaire;

    @Column(name = "endpoint", nullable = false, length = 255)
    private String endpoint;

    @Column(name = "methode", nullable = false, length = 10)
    private String methode;

    @Column(name = "statut", nullable = false, length = 20)
    private String statut;

    @Column(name = "code_statut_http")
    private Integer codeStatutHttp;

    @Lob
    @Column(name = "request_body")
    private String requestBody;

    @Lob
    @Column(name = "response_body")
    private String responseBody;

    @Column(name = "ip_source", length = 45)
    private String ipSource;

    @Column(name = "date_appel", nullable = false)
    private LocalDateTime dateAppel;

    @Column(name = "duree_ms")
    private Long dureeMs;

    @Column(name = "id_utilisateur")
    private Long idUtilisateur;

    @Lob
    @Column(name = "erreur_message")
    private String erreurMessage;
}
