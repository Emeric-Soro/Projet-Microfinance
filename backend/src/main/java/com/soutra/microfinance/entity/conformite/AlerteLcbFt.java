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

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "soutra_alerte_lcbft")
public class AlerteLcbFt extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerte")
    private Long idAlerte;

    @Column(name = "id_client")
    private Long idClient;

    @Column(name = "type_alerte", nullable = false, length = 100)
    private String typeAlerte;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "niveau_risque", nullable = false, length = 20)
    private String niveauRisque;

    @Column(name = "statut", nullable = false, length = 20)
    private String statut = "OUVERTE";

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @Column(name = "traite_par", length = 100)
    private String traitePar;

    @Lob
    @Column(name = "actions")
    private String actions;
}
