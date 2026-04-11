package com.microfinance.core_banking.repository.client;

import com.microfinance.core_banking.entity.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

	// Recherche unique par login utilisateur.
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
}
