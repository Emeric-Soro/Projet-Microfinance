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
@Table(name = "type_canal")
// Entite representant un canal d'envoi (SMS, email, etc.).
public class TypeCanal extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_canal")
	// Identifiant unique du canal.
	private Long idCanal;

	@Column(name = "code_canal", nullable = false, length = 50, unique = true)
	// Code unique du canal.
	private String codeCanal;

	@Column(nullable = false, length = 100)
	// Libelle descriptif du canal.
	private String libelle;

	@OneToMany(mappedBy = "typeCanal", targetEntity = Notification.class)
	// Notifications envoyees via ce canal.
	private List<Notification> notifications = new ArrayList<>();
}
