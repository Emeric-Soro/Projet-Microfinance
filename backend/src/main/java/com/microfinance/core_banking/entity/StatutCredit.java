package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "statut_credit")
// Entite representant un statut possible dans le cycle de vie d'un credit.
public class StatutCredit extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_statut_credit")
	// Identifiant unique du statut.
	private Long idStatutCredit;

	@Column(name = "code_statut", nullable = false, length = 50, unique = true)
	// Code unique du statut (ex: EN_COURS, EN_RETARD).
	private String codeStatut;

	@Column(nullable = false, length = 100)
	// Libelle du statut.
	private String libelle;

	@OneToMany(mappedBy = "statutCredit")
	// Credits ayant ce statut.
	private List<Credit> credits = new ArrayList<>();
}
