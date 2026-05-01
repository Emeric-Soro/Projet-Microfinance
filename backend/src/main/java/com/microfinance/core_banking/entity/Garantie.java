package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "garantie")
// Entite representant une garantie associee a un credit.
public class Garantie extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_garantie")
	// Identifiant unique de la garantie.
	private Long idGarantie;

	@Enumerated(EnumType.STRING)
	@Column(name = "type_garantie", nullable = false, length = 30)
	// Type de la garantie (caution solidaire, nantissement, etc.).
	private TypeGarantie typeGarantie;

	@Column(nullable = false, length = 500)
	// Description detaillee de la garantie.
	private String description;

	@Column(name = "valeur_estimee", precision = 19, scale = 2)
	// Valeur estimee de la garantie en FCFA.
	private BigDecimal valeurEstimee;

	@Column(name = "est_active", nullable = false)
	// Indique si la garantie est toujours en vigueur.
	private Boolean estActive = true;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_credit", nullable = false)
	// Credit securise par cette garantie.
	private Credit credit;
}
