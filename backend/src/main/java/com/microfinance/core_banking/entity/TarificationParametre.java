package com.microfinance.core_banking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tarification_parametre")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TarificationParametre extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametre")
    private Long idParametre;

    @Column(name = "cle_parametre", nullable = false, length = 100, unique = true)
    private String cleParametre;

    @Column(name = "valeur_parametre", nullable = false, length = 100)
    private String valeurParametre;

    @Column(name = "description_parametre", length = 255)
    private String descriptionParametre;
}
