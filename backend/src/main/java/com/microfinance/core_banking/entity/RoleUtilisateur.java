package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role_utilisateur")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
// Entite representant un role de securite utilisateur.
public class RoleUtilisateur extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_role")
	// Identifiant unique du role.
	private Long idRole;

	@Column(name = "code_role_utilisateur", nullable = false, length = 50, unique = true)
	// Code unique du role.
	private String codeRoleUtilisateur;

	@Column(name = "intitule_role", nullable = false, length = 100)
	// Libelle metier du role.
	private String intituleRole;

	@ManyToMany(mappedBy = "roles")
	// Utilisateurs possedant ce role.
	private Set<Utilisateur> utilisateurs = new HashSet<>();

	@ManyToMany
	@JoinTable(
			name = "role_permission_securite",
			joinColumns = @JoinColumn(name = "id_role"),
			inverseJoinColumns = @JoinColumn(name = "id_permission")
	)
	private Set<PermissionSecurite> permissions = new HashSet<>();
}
