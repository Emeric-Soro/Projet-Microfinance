package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
// Entite representant une transaction effectuee sur un ou plusieurs comptes.
public class Transaction extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_transaction")
	// Identifiant unique de la transaction.
	private Integer idTransaction;

	@Column(name = "reference_unique", nullable = false, length = 80, unique = true)
	// Reference unique metier de la transaction.
	private String referenceUnique;

	@Column(name = "date_heure_transaction", nullable = false)
	// Date et heure de la transaction.
	private LocalDateTime dateHeureTransaction;

	@Column(name = "montant_global", nullable = false)
	// Montant total de la transaction.
	private Double montantGlobal;

	@Column(nullable = false)
	// Frais appliques a la transaction.
	private Integer frais;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_user", nullable = false)
	// Utilisateur ayant initie la transaction.
	private Utilisateur utilisateur;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_type_transaction", nullable = false)
	// Type associe a la transaction.
	private TypeTransaction typeTransaction;

	@OneToMany(mappedBy = "transaction", targetEntity = LigneEcriture.class)
	// Lignes comptables composees par la transaction.
	private List<LigneEcriture> lignesEcriture = new ArrayList<>();
}
