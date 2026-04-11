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
@Table(name = "type_agio")
// Entite representant un type d'agio.
public class TypeAgio extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_type_agio")
	// Identifiant unique du type d'agio.
	private Long idTypeAgio;

	@Column(name = "code_type_agio", nullable = false, length = 50, unique = true)
	// Code unique du type d'agio.
	private String codeTypeAgio;

	@Column(nullable = false, length = 100)
	// Libelle du type d'agio.
	private String libelle;

	@OneToMany(mappedBy = "typeAgio", targetEntity = Agio.class)
	// Agios rattaches a ce type.
	private List<Agio> agios = new ArrayList<>();
}
