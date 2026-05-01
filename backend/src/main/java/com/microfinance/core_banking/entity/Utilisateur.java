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
import java.time.LocalDateTime;
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

	@Column(nullable = false)
	// Active ou desactive l'acces numerique.
	private Boolean actif = Boolean.TRUE;

	@Column(name = "compte_expire_le")
	// Date d'expiration du compte applicatif.
	private LocalDateTime compteExpireLe;

	@Column(name = "compte_verrouille_jusqu_au")
	// Date jusqu'a laquelle le compte reste verrouille.
	private LocalDateTime compteVerrouilleJusquAu;

	@Column(name = "nombre_echecs_connexion", nullable = false)
	// Compteur d'echecs consecutifs de connexion.
	private Integer nombreEchecsConnexion = 0;

	@Column(name = "dernier_echec_connexion")
	// Date du dernier echec de connexion.
	private LocalDateTime dernierEchecConnexion;

	@Column(name = "derniere_connexion_reussie")
	// Date de la derniere connexion reussie.
	private LocalDateTime derniereConnexionReussie;

	@Column(name = "mot_de_passe_modifie_le", nullable = false)
	// Date du dernier changement de mot de passe.
	private LocalDateTime motDePasseModifieLe;

	@Column(name = "identifiants_expirent_le", nullable = false)
	// Date d'expiration des identifiants.
	private LocalDateTime identifiantsExpirentLe;

	@Column(name = "second_facteur_active", nullable = false)
	// Indique si le deuxieme facteur est impose.
	private Boolean secondFacteurActive = Boolean.TRUE;

	@Column(name = "otp_challenge_id", length = 80)
	// Identifiant de challenge OTP en cours.
	private String otpChallengeId;

	@Column(name = "otp_hash", length = 255)
	// Hash du dernier OTP emis.
	private String otpHash;

	@Column(name = "otp_expire_le")
	// Date d'expiration du challenge OTP.
	private LocalDateTime otpExpireLe;

	@Column(name = "otp_tentatives_restantes", nullable = false)
	// Nombre de tentatives OTP restantes.
	private Integer otpTentativesRestantes = 0;

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
	// Retourne le mot de passe encode de cet utilisateur (implémentation explicite
	// pour garantir la compatibilité lors de la compilation lorsque Lombok n'est
	// pas pris en compte par certains toolchains de compilation dans des conteneurs).
	public String getPassword() {
		return password;
	}

	@Override
	// Le compte applicatif peut expirer a une date fixee.
	public boolean isAccountNonExpired() {
		return compteExpireLe == null || compteExpireLe.isAfter(LocalDateTime.now());
	}

	@Override
	// Le compte reste verrouille tant que la date de lockout n'est pas depassee.
	public boolean isAccountNonLocked() {
		return compteVerrouilleJusquAu == null || compteVerrouilleJusquAu.isBefore(LocalDateTime.now());
	}

	@Override
	// Les identifiants expirent selon la politique de securite.
	public boolean isCredentialsNonExpired() {
		return identifiantsExpirentLe == null || identifiantsExpirentLe.isAfter(LocalDateTime.now());
	}

	@Override
	// L'utilisateur doit etre actif et ne pas etre rattache a un client bloque.
	public boolean isEnabled() {
		return Boolean.TRUE.equals(actif) && clientActif();
	}

	private boolean clientActif() {
		if (client == null || client.getStatutClient() == null || client.getStatutClient().getLibelleStatut() == null) {
			return true;
		}

		String statut = client.getStatutClient().getLibelleStatut().trim().toUpperCase();
		return !Set.of("BLOQUE", "SUSPENDU", "INACTIF", "FRAUDE").contains(statut);
	}
}
