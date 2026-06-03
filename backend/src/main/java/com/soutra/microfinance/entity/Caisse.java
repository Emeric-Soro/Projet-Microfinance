package com.soutra.microfinance.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "soutra_caisse")
public class Caisse extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caisse")
    private Long idCaisse;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    @JsonIgnore
    private Utilisateur utilisateur;

    @Column(name = "solde_ouverture", nullable = false, precision = 19, scale = 2)
    private BigDecimal soldeOuverture;

    @Column(name = "solde_courant", nullable = false, precision = 19, scale = 2)
    private BigDecimal soldeCourant;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutCaisse statut;

    @Column(name = "date_ouverture", nullable = false)
    private LocalDateTime dateOuverture;

    @Column(name = "date_fermeture")
    private LocalDateTime dateFermeture;

    @Column(name = "ecart_fermeture", precision = 19, scale = 2)
    private BigDecimal ecartFermeture;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 0;

    public enum StatutCaisse {
        OUVERTE, FERMEE
    }
}
