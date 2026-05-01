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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "historique_mot_de_passe_utilisateur")
public class HistoriqueMotDePasseUtilisateur extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique_mdp")
    private Long idHistoriqueMotDePasse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "hash_mot_de_passe", nullable = false, length = 255)
    private String hashMotDePasse;

    @Column(name = "date_changement", nullable = false)
    private LocalDateTime dateChangement;

    @Column(name = "motif", length = 255)
    private String motif;
}
