package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "declaration_fiscale")
public class DeclarationFiscale extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_declaration")
    private Long idDeclaration;

    @Column(name = "reference_declaration", nullable = false, unique = true, length = 60)
    private String referenceDeclaration;

    @Column(name = "type_declaration", nullable = false, length = 30)
    private String typeDeclaration;

    @Column(name = "periode_debut", nullable = false)
    private LocalDate periodeDebut;

    @Column(name = "periode_fin", nullable = false)
    private LocalDate periodeFin;

    @Column(name = "montant_base", precision = 19, scale = 2)
    private BigDecimal montantBase = BigDecimal.ZERO;

    @Column(name = "montant_taxe", precision = 19, scale = 2)
    private BigDecimal montantTaxe = BigDecimal.ZERO;

    @Column(nullable = false, length = 30)
    private String statut = "BROUILLON";

    @Column(name = "date_declaration")
    private LocalDate dateDeclaration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id")
    private Agence agence;
}
