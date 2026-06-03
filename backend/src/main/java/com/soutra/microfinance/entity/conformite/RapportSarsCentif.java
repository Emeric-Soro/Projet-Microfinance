package com.soutra.microfinance.entity.conformite;

import com.soutra.microfinance.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "soutra_rapport_sars_centif")
public class RapportSarsCentif extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rapport")
    private Long idRapport;

    @Column(name = "reference", nullable = false, length = 50, unique = true)
    private String reference;

    @Column(name = "id_client")
    private Long idClient;

    @Column(name = "type_alerte", nullable = false, length = 100)
    private String typeAlerte;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "montant_soupconne", precision = 19, scale = 2)
    private BigDecimal montantSoupconne;

    @Column(name = "statut", nullable = false, length = 30)
    private String statut = "NOUVEAU";

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @Column(name = "soumis_par", length = 100)
    private String soumisPar;

    @Column(name = "traite_par", length = 100)
    private String traitePar;

    @Lob
    @Column(name = "motif_rejet")
    private String motifRejet;

    @Column(name = "transmission_centif")
    private Boolean transmissionCentif = false;
}
