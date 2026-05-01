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
@Table(name = "echeance")
// Entite representant une ligne du tableau d'amortissement d'un credit.
public class Echeance extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_echeance")
	// Identifiant unique de l'echeance.
	private Long idEcheance;

	@Column(name = "numero_echeance", nullable = false)
	// Numero sequentiel de l'echeance (1, 2, 3...).
	private Integer numeroEcheance;

	@Column(name = "date_echeance", nullable = false)
	// Date a laquelle l'echeance est due.
	private LocalDate dateEcheance;

	@Column(name = "montant_capital", nullable = false, precision = 19, scale = 2)
	// Part de capital a rembourser dans cette echeance.
	private BigDecimal montantCapital;

	@Column(name = "montant_interet", nullable = false, precision = 19, scale = 2)
	// Part d'interets dans cette echeance.
	private BigDecimal montantInteret;

	@Column(name = "montant_total", nullable = false, precision = 19, scale = 2)
	// Montant total de l'echeance (capital + interets).
	private BigDecimal montantTotal;

	@Column(name = "montant_penalite", precision = 19, scale = 2)
	// Penalites eventuelles en cas de retard.
	private BigDecimal montantPenalite;

	@Column(name = "montant_paye", nullable = false, precision = 19, scale = 2)
	// Montant effectivement paye par le client.
	private BigDecimal montantPaye;

	@Column(name = "date_paiement")
	// Date effective du paiement de l'echeance.
	private LocalDate datePaiement;

	@Column(name = "est_payee", nullable = false)
	// Indique si l'echeance a ete integralement payee.
	private Boolean estPayee = false;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_credit", nullable = false)
	// Credit auquel appartient cette echeance.
	private Credit credit;
}
