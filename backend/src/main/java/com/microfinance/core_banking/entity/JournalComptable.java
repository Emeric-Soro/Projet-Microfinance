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
@Table(name = "journal_comptable")
public class JournalComptable extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_journal_comptable")
    private Long idJournalComptable;

    @Column(name = "code_journal", nullable = false, unique = true, length = 20)
    private String codeJournal;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(name = "type_journal", nullable = false, length = 40)
    private String typeJournal;

    @Column(nullable = false)
    private Boolean actif = Boolean.TRUE;
}

