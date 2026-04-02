package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "client")
// Entite representant un client de la microfinance.
public class Client extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_client")
	// Identifiant unique du client.
	private Long idClient;

	@Column(name = "code_client", nullable = false, length = 50, unique = true)
	// Code unique du client.
	private String codeClient;

	@Column(nullable = false, length = 100)
	// Nom du client.
	private String nom;

	@Column(nullable = false, length = 100)
	// Prenom du client.
	private String prenom;

	@Column(name = "date_naissance")
	// Date de naissance du client.
	private LocalDate dateNaissance;

	@Column
	// Adresse principale du client.
	private String adresse;

	@Column(length = 30)
	// Numero de telephone du client.
	private String telephone;

	@Column(length = 150)
	// Adresse email du client.
	private String email;

	@Column(name = "date_inscription", nullable = false)
	// Date d'inscription du client.
	private LocalDate dateInscription;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_statut_client", nullable = false)
	// Statut courant du client.
	private StatutClient statutClient;

	@OneToOne(mappedBy = "client")
	// Utilisateur applicatif lie a ce client.
	private Utilisateur utilisateur;

	@OneToMany(mappedBy = "client")
	// Notifications recues par ce client.
	private List<Notification> notifications = new ArrayList<>();

	@OneToMany(mappedBy = "client")
	// Comptes appartenant a ce client.
	private List<Compte> comptes = new ArrayList<>();
}
