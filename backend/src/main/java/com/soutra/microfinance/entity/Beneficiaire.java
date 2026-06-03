package com.soutra.microfinance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "soutra_beneficiaire", indexes = {
        @Index(name = "ix_soutra_beneficiaire_client", columnList = "id_client")
})
@Getter
@Setter
@NoArgsConstructor
public class Beneficiaire extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_beneficiaire")
    private Long idBeneficiaire;

    @Column(name = "id_client", nullable = false)
    private Long idClient;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", length = 100)
    private String prenom;

    @Column(name = "compte_beneficiaire", nullable = false, length = 50)
    private String compteBeneficiaire;

    @Column(name = "banque", length = 100)
    private String banque;
}
