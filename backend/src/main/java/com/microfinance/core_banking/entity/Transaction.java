package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bank_transaction")
// Entite representant une transaction effectuee sur un ou plusieurs comptes.
public class Transaction extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_transaction")
	private Long idTransaction;

	@Column(name = "reference_unique", nullable = false, length = 80, unique = true)
	// Reference unique metier de la transaction.
	private String referenceUnique;

	@Column(name = "date_heure_transaction", nullable = false)
	// Date et heure de la transaction.
	private LocalDateTime dateHeureTransaction;

	@Column(name = "montant_global", nullable = false, precision = 19, scale = 2)
	// Montant total de la transaction.
	private BigDecimal montantGlobal;

	@Column(nullable = false)
	// Frais appliques a la transaction.
	private BigDecimal frais;

	@Enumerated(EnumType.STRING)
	@Column(name = "statut_operation", nullable = false, length = 30)
	// Statut metier de l'operation.
	private StatutOperation statutOperation = StatutOperation.EN_ATTENTE;

	@Column(name = "validation_superviseur_requise", nullable = false)
	// Indique si l'operation doit etre approuvee par un superviseur.
	private Boolean validationSuperviseurRequise = Boolean.FALSE;

	@Column(name = "date_validation")
	// Date de validation ou rejet superviseur.
	private LocalDateTime dateValidation;

	@Column(name = "date_execution")
	// Date de comptabilisation effective.
	private LocalDateTime dateExecution;

	@Column(name = "motif_rejet", length = 500)
	// Motif de rejet si l'operation est refusee.
	private String motifRejet;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_user", nullable = false)
	// Utilisateur ayant initie la transaction.
	private Utilisateur utilisateur;

	@ManyToOne
	@JoinColumn(name = "id_user_validation")
	// Superviseur ayant valide ou rejete la transaction.
	private Utilisateur utilisateurValidation;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_type_transaction", nullable = false)
	// Type associe a la transaction.
	private TypeTransaction typeTransaction;

	@ManyToOne
	@JoinColumn(name = "id_compte_source")
	// Compte source implique dans l'operation.
	private Compte compteSource;

	@ManyToOne
	@JoinColumn(name = "id_compte_destination")
	// Compte destination implique dans l'operation.
	private Compte compteDestination;

	@OneToMany(mappedBy = "transaction", targetEntity = LigneEcriture.class)
	// Lignes comptables composees par la transaction.
	private List<LigneEcriture> lignesEcriture = new ArrayList<>();
}
