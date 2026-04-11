package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
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
@Table(name = "statut_compte")
// Entite representant l'historique de statut d'un compte.
public class StatutCompte extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_statut_compte")
	// Identifiant unique du statut de compte.
	private Long idStatutCompte;

	@Column(name = "libelle_statut", nullable = false, length = 100)
	// Libelle du statut du compte.
	private String libelleStatut;

	@Column(name = "date_statut", nullable = false)
	// Date et heure d'application du statut.
	private LocalDateTime dateStatut;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_compte", nullable = false)
	// Compte concerne par ce statut.
	private Compte compte;
}
