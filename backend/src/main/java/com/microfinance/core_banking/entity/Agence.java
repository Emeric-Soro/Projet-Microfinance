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
@Table(name = "agence")
// Entite representant un point de service ou une agence de la microfinance.
public class Agence extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_agence")
	// Identifiant unique de l'agence.
	private Long idAgence;

	@Column(name = "code_agence", nullable = false, length = 20, unique = true)
	// Code unique de l'agence (ex: AG-001).
	private String codeAgence;

	@Column(nullable = false, length = 150)
	// Nom de l'agence.
	private String nom;

	@Column(length = 255)
	// Adresse physique de l'agence.
	private String adresse;

	@Column(length = 30)
	// Numero de telephone de l'agence.
	private String telephone;

	@Column(name = "est_active", nullable = false)
	// Indique si l'agence est actuellement en activite.
	private Boolean estActive = true;

	@OneToMany(mappedBy = "agence")
	// Comptes geres par cette agence.
	private List<Compte> comptes = new ArrayList<>();
}
