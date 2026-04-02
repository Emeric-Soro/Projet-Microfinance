package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "utilisateur")
// Entite representant un utilisateur applicatif lie a un client.
public class Utilisateur extends BaseAuditEntity {

	@Id
	@Column(name = "id_user", length = 50)
	// Identifiant technique de l'utilisateur.
	private String idUser;

	@Column(nullable = false, length = 100, unique = true)
	// Nom de connexion de l'utilisateur.
	private String login;

	@Column(nullable = false, length = 255)
	// Mot de passe de l'utilisateur.
	private String password;

	@OneToOne(optional = false)
	@JoinColumn(name = "id_client", nullable = false, unique = true)
	// Client associe a cet utilisateur.
	private Client client;

	@ManyToMany
	@JoinTable(
			name = "utilisateur_role",
			joinColumns = @JoinColumn(name = "id_user"),
			inverseJoinColumns = @JoinColumn(name = "id_role")
	)
	// Roles attribues a l'utilisateur.
	private Set<RoleUtilisateur> roles = new HashSet<>();

	@OneToMany(mappedBy = "utilisateur")
	// Transactions creees par l'utilisateur.
	private List<Transaction> transactions = new ArrayList<>();
}
