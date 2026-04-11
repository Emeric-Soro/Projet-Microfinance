package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
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
	private Long idCarte;

	@Column(name = "numero_carte", nullable = false, length = 40, unique = true)
	// Numero unique de la carte.
	private String numeroCarte;

	@Column(name = "date_expiration", nullable = false)
	// Date d'expiration de la carte.
	private LocalDate dateExpiration;

	@Column(name = "cvv", nullable = false, length = 3)
	// Code de securite de la carte (CVV).
	private String cvv;

	@Column(nullable = false)
	// Etat actif ou inactif de la carte.
	private Boolean statut;

	@Column(name = "plafond_journalier", nullable = false, precision = 19, scale = 2)
	// Montant maximal autorise par jour.
	private BigDecimal plafondJournalier;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_compte", nullable = false)
	// Compte auquel la carte est rattachee.
	private Compte compte;
}
