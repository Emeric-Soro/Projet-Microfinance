package com.soutra.microfinance.entity.conformite;

import com.soutra.microfinance.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "soutra_consentement_rgpd")
public class ConsentementRgpd extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consentement")
    private Long idConsentement;

    @Column(name = "id_client")
    private Long idClient;

    @Column(name = "finalite", nullable = false, length = 200)
    private String finalite;

    @Column(name = "consenti")
    private Boolean consenti = false;

    @Column(name = "date_consentement")
    private LocalDateTime dateConsentement;

    @Column(name = "date_expiration")
    private LocalDateTime dateExpiration;

    @Column(name = "adresse_ip", length = 50)
    private String adresseIp;
}
