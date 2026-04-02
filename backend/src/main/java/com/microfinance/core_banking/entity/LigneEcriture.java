package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ligne_ecriture")
// Detail comptable d'une transaction (debit ou credit).
public class LigneEcriture extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_ligne")
	// Identifiant unique de la ligne d'ecriture.
	private Integer idLigne;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	// Sens comptable de la ligne (debit ou credit).
	private SensEcriture sens;

	@Column(nullable = false)
	// Montant porte par cette ligne.
	private Double montant;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_transaction", nullable = false)
	// Transaction a laquelle appartient la ligne.
	private Transaction transaction;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_compte", nullable = false)
	// Compte impacte par la ligne.
	private Compte compte;
}
