package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "taux_fiscal")
public class TauxFiscal extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_taux")
    private Long idTaux;

    @Column(name = "code_taxe", nullable = false, length = 20)
    private String codeTaxe;

    @Column(nullable = false, length = 200)
    private String libelle;

    @Column(nullable = false, precision = 7, scale = 4)
    private BigDecimal taux;

    @Column(name = "type_operation", length = 50)
    private String typeOperation;

    @Column(name = "seuil_applicable", precision = 19, scale = 2)
    private BigDecimal seuilApplicable;

    @Column(name = "date_effet", nullable = false)
    private LocalDate dateEffet;

    @Column(nullable = false)
    private Boolean actif = true;
}
