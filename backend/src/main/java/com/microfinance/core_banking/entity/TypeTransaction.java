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
@Table(name = "type_transaction")
// Entite representant le type d'une transaction.
public class TypeTransaction extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_type_transaction")
	// Identifiant unique du type de transaction.
	private Integer idTypeTransaction;

	@Column(name = "code_type_transaction", nullable = false, length = 50, unique = true)
	// Code unique du type de transaction.
	private String codeTypeTransaction;

	@Column(nullable = false, length = 100)
	// Libelle du type de transaction.
	private String libelle;

	@OneToMany(mappedBy = "typeTransaction")
	// Transactions rattachees a ce type.
	private List<Transaction> transactions = new ArrayList<>();
}
