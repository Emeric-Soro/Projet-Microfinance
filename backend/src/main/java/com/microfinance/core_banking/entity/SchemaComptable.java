package com.microfinance.core_banking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "schema_comptable")
public class SchemaComptable extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_schema_comptable")
    private Long idSchemaComptable;

    @Column(name = "code_operation", nullable = false, unique = true, length = 40)
    private String codeOperation;

    @Column(name = "compte_debit", nullable = false, length = 30)
    private String compteDebit;

    @Column(name = "compte_credit", nullable = false, length = 30)
    private String compteCredit;

    @Column(name = "compte_frais", length = 30)
    private String compteFrais;

    @Column(name = "journal_code", length = 20)
    private String journalCode;

    @Column(nullable = false)
    private Boolean actif = Boolean.TRUE;
}

