package com.soutra.microfinance.repository.client;

import com.soutra.microfinance.entity.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.soutra.microfinance.entity.RoleUtilisateur;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

	// Recherche unique par login utilisateur.
	@EntityGraph(attributePaths = {"roles", "client", "client.statutClient"})
	Optional<Utilisateur> findByLogin(String login);
	// Verification rapide de si un utilisateur existe par login.
	boolean existsByLogin(String login);

	// Recherche de l'utilisateur associe a un client.
	Optional<Utilisateur> findByClient_IdClient(Long idClient);
	// Verification rapide de si un client possede deja un utilisateur.
	boolean existsByClient_IdClient(Long idClient);

	// Liste paginee des utilisateurs possedant un role donne.
	Page<Utilisateur> findByRoles_IdRole(Long idRole, Pageable pageable);

	// Liste paginee des utilisateurs crees entre deux dates.
	Page<Utilisateur> findByCreatedAtBetween(LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable);

	// Liste paginee des utilisateurs avec un role specifique.
	Page<Utilisateur> findByRolesContaining(RoleUtilisateur role, Pageable pageable);

	// Liste paginee des utilisateurs filtres par statut actif/inactif.
	Page<Utilisateur> findByActif(Boolean actif, Pageable pageable);

	// Recherche d'un utilisateur par email de son client associe.
	Optional<Utilisateur> findByClient_EmailIgnoreCase(String email);

	// Recherche d'un utilisateur par son token de reinitialisation non expire.
	@EntityGraph(attributePaths = {"client"})
	Optional<Utilisateur> findFirstByResetTokenHashAndResetTokenExpireLeAfter(
			String resetTokenHash, LocalDateTime now);
}
