package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

	@Column(nullable = false)
	// Etat actif ou inactif de la carte.
	private Boolean statut;

	@Column(name = "plafond_journalier", nullable = false, precision = 19, scale = 2)
	// Montant maximal autorise par jour.
	private BigDecimal plafondJournalier;

	@Column(name = "type_carte", length = 20)
	private String typeCarte = "DEBIT";

	@Column(name = "plafond_mensuel", precision = 19, scale = 2)
	private BigDecimal plafondMensuel;

	@Column(name = "solde_prepaye", precision = 19, scale = 2)
	private BigDecimal soldePrepaye = BigDecimal.ZERO;

	@Column(name = "pin_hash", length = 255)
	private String pinHash;

	@Column(name = "tentative_pin")
	private Integer tentativePin = 0;

	@Column(nullable = false)
	private Boolean bloque = false;

	@Column(name = "token_carte", length = 255)
	private String tokenCarte;

	@Column(name = "date_derniere_utilisation")
	private LocalDateTime dateDerniereUtilisation;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_compte", nullable = false)
	// Compte auquel la carte est rattachee.
	private Compte compte;
}
