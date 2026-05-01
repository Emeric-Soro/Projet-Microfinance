package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification")
// Entite representant une notification envoyee a un client.
public class Notification extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_notif")
	// Identifiant unique de la notification.
	private Long idNotif;

	@Column(nullable = false, length = 500)
	// Contenu textuel du message a envoyer.
	private String message;

	@Column(name = "date_envoi")
	// Date d'envoi effectif de la notification lorsqu'elle a abouti.
	private LocalDate dateEnvoi;

	@Column(name = "erreur_envoi", length = 500)
	// Motif du dernier echec d'envoi si la notification n'a pas abouti.
	private String erreurEnvoi;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_canal", nullable = false)
	// Canal utilise pour l'envoi.
	private TypeCanal typeCanal;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_statut_envoi", nullable = false)
	// Statut courant de l'envoi.
	private StatutEnvoi statutEnvoi;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_client", nullable = false)
	// Client destinataire de la notification.
	private Client client;
}
