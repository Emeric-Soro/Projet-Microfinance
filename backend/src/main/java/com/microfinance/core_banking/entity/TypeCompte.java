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
@Table(name = "type_compte")
// Entite representant un type de compte.
public class TypeCompte extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_type_compte")
	// Identifiant unique du type de compte.
	private Long idTypeCompte;

	@Column(nullable = false, length = 100)
	// Libelle du type de compte.
	private String libelle;

	@OneToMany(mappedBy = "typeCompte")
	// Comptes classes sous ce type.
	private List<Compte> comptes = new ArrayList<>();
}
