package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "carte_visa")
// Entite representant une carte associee a un compte.
public class CarteVisa extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_carte")
	// Identifiant unique de la carte.
	private Integer idCarte;

	@Column(name = "numero_carte", nullable = false, length = 40, unique = true)
	// Numero unique de la carte.
	private String numeroCarte;

	@Column(name = "date_expiration", nullable = false)
	// Date d'expiration de la carte.
	private LocalDate dateExpiration;

	@Column(nullable = false)
	// Etat actif ou inactif de la carte.
	private Boolean statut;

	@Column(name = "plafond_journalier", nullable = false)
	// Montant maximal autorise par jour.
	private Double plafondJournalier;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_compte", nullable = false)
	// Compte auquel la carte est rattachee.
	private Compte compte;
}
