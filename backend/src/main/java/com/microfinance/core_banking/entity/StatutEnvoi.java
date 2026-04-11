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
@Table(name = "statut_envoi")
// Entite representant le statut d'envoi d'une notification.
public class StatutEnvoi extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_statut_envoi")
	// Identifiant unique du statut d'envoi.
	private Long idStatutEnvoi;

	@Column(name = "code_statut_envoi", nullable = false, length = 50, unique = true)
	// Code unique du statut d'envoi.
	private String codeStatutEnvoi;

	@Column(nullable = false, length = 100)
	// Libelle du statut d'envoi.
	private String libelle;

	@OneToMany(mappedBy = "statutEnvoi")
	// Notifications ayant ce statut.
	private List<Notification> notifications = new ArrayList<>();
}
