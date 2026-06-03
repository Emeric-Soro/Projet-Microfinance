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
@Table(name = "soutra_reclamation")
public class Reclamation extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reclamation")
    private Long idReclamation;

    @Column(name = "reference", nullable = false, length = 50, unique = true)
    private String reference;

    @Column(name = "id_client")
    private Long idClient;

    @Column(name = "type_reclamation", nullable = false, length = 100)
    private String typeReclamation;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "statut", nullable = false, length = 30)
    private String statut = "NOUVEAU";

    @Column(name = "priorite", nullable = false, length = 20)
    private String priorite = "NORMALE";

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @Column(name = "cree_par", length = 100)
    private String creePar;

    @Column(name = "traite_par", length = 100)
    private String traitePar;

    @Lob
    @Column(name = "motif_cloture")
    private String motifCloture;
}
