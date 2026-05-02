package com.microfinance.core_banking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "billetage_caisse")
public class BilletageCaisse extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_billetage")
    private Long idBilletage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_session_caisse", nullable = false)
    private SessionCaisse sessionCaisse;

    @Column(name = "coupure", nullable = false, precision = 19, scale = 2)
    private BigDecimal coupure;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "total", nullable = false, precision = 19, scale = 2)
    private BigDecimal total;

    @Column(name = "type_billetage", nullable = false, length = 20)
    private String typeBilletage = "BILLET";

    @Column(name = "date_billetage", nullable = false)
    private LocalDateTime dateBilletage;
}
