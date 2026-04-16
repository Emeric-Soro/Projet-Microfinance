package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "utilisateur")
// Entite representant un utilisateur applicatif lie a un client.
public class Utilisateur extends BaseAuditEntity implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_user")
	// Identifiant technique de l'utilisateur.
	private Long idUser;

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

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "utilisateur_role",
			joinColumns = @JoinColumn(name = "id_user"),
			inverseJoinColumns = @JoinColumn(name = "id_role")
	)
	// Roles attribues a l'utilisateur.
	private Set<RoleUtilisateur> roles = new HashSet<>();

	@OneToMany(mappedBy = "utilisateur")
	// Transaction creees par l'utilisateur.
	private List<Transaction> transactions = new ArrayList<>();

	@Override
	// Transforme les roles metier en autorites Spring Security.
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream()
				.map(RoleUtilisateur::getCodeRoleUtilisateur)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toSet());
	}

	@Override
	// Spring Security utilise login comme username de reference.
	public String getUsername() {
		return login;
	}

	@Override
	// Retourne true: gestion fine des statuts non encore implementee.
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	// Retourne true: verrouillage de compte non encore implemente.
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	// Retourne true: expiration des identifiants non encore implementee.
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	// Retourne true: activation/desactivation utilisateur non encore implementee.
	public boolean isEnabled() {
		return true;
	}
}
