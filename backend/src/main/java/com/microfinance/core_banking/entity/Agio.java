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
@Table(name = "agio")
// Entite representant les frais d'agio d'un compte.
public class Agio extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_agio")
	// Identifiant unique de l'agio.
	private Long idAgio;

	@Column(nullable = false, precision = 19, scale = 2)
	// Montant de l'agio a prelever.
	private BigDecimal montant;

	@Column(name = "date_calcul", nullable = false)
	// Date de calcul de l'agio.
	private LocalDate dateCalcul;

	@Column(name = "est_preleve", nullable = false)
	// Indique si l'agio a deja ete preleve.
	private Boolean estPreleve;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_compte", nullable = false)
	// Compte sur lequel l'agio est applique.
	private Compte compte;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_type_agio", nullable = false)
	// Type d'agio applique.
	private TypeAgio typeAgio;
}
