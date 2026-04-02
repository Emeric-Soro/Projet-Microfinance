package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "statut_client")
// Entite representant l'etat d'un client.
public class StatutClient extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_statut_client")
	// Identifiant unique du statut client.
	private Long idStatutClient;

	@Column(name = "libelle_statut", nullable = false, length = 100)
	// Libelle du statut applique au client.
	private String libelleStatut;

	@Column(name = "date_statut", nullable = false)
	// Date et heure d'effet du statut.
	private LocalDateTime dateStatut;

	@OneToMany(mappedBy = "statutClient")
	// Clients associes a ce statut.
	private List<Client> clients = new ArrayList<>();
}
