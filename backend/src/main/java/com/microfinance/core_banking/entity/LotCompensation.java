package com.microfinance.core_banking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "lot_compensation")
public class LotCompensation extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lot_compensation")
    private Long idLotCompensation;

    @Column(name = "reference_lot", nullable = false, unique = true, length = 80)
    private String referenceLot;

    @Column(name = "type_lot", nullable = false, length = 40)
    private String typeLot;

    @Column(nullable = false, length = 30)
    private String statut = "INITIE";

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @Column(length = 500)
    private String commentaire;
}
