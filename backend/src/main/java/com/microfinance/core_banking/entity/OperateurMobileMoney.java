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
@Table(name = "operateur_mobile_money")
public class OperateurMobileMoney extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_operateur_mobile_money")
    private Long idOperateurMobileMoney;

    @Column(name = "code_operateur", nullable = false, unique = true, length = 30)
    private String codeOperateur;

    @Column(name = "nom_operateur", nullable = false, length = 120)
    private String nomOperateur;

    @Column(nullable = false, length = 30)
    private String statut = "ACTIF";
}

