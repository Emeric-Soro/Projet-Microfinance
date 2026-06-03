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
@Table(name = "soutra_pep")
public class PersonnePolitiquementExposee extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pep")
    private Long idPep;

    @Column(name = "id_client")
    private Long idClient;

    @Column(name = "nom_complet", nullable = false, length = 200)
    private String nomComplet;

    @Column(name = "fonction", length = 200)
    private String fonction;

    @Column(name = "pays", length = 100)
    private String pays;

    @Column(name = "niveau_risque", length = 20)
    private String niveauRisque = "FAIBLE";

    @Column(name = "date_declaration", nullable = false)
    private LocalDateTime dateDeclaration;

    @Column(name = "source_information", length = 200)
    private String sourceInformation;

    @Column(name = "verifie_par", length = 100)
    private String verifiePar;

    @Column(name = "date_verification")
    private LocalDateTime dateVerification;

    @Column(name = "statut", nullable = false, length = 20)
    private String statut = "ACTIF";
}
