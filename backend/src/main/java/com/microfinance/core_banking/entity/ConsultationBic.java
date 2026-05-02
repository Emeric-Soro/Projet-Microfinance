package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "consultation_bic")
public class ConsultationBic extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consultation")
    private Long idConsultation;

    @Column(name = "reference_client", length = 50)
    private String referenceClient;

    @Column(name = "numero_piece", length = 50)
    private String numeroPiece;

    @Column(name = "encours_total", precision = 19, scale = 2)
    private BigDecimal encoursTotal = BigDecimal.ZERO;

    @Column(name = "encours_sain", precision = 19, scale = 2)
    private BigDecimal encoursSain = BigDecimal.ZERO;

    @Column(name = "encours_impaye", precision = 19, scale = 2)
    private BigDecimal encoursImpaye = BigDecimal.ZERO;

    @Column(name = "nombre_creances")
    private Integer nombreCreances = 0;

    @Column(name = "statut_bic", length = 20)
    private String statutBic;

    @Column(name = "date_consultation", nullable = false)
    private LocalDate dateConsultation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;
}
