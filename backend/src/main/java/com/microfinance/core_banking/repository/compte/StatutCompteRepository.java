package com.microfinance.core_banking.repository.compte;

import com.microfinance.core_banking.entity.StatutCompte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface StatutCompteRepository extends JpaRepository<StatutCompte, Long> {

	// Recherche par libelle exact de statut (insensible a la casse).
	Optional<StatutCompte> findByLibelleStatutIgnoreCase(String libelleStatut);
	// Verification rapide de si un statut de compte existe par libelle.
	boolean existsByLibelleStatutIgnoreCase(String libelleStatut);

	// Liste paginee des statuts d'un compte.
	Page<StatutCompte> findByCompte_IdCompte(Long idCompte, Pageable pageable);

	// Dernier statut connu d'un compte (ordre chronologique descendant).
	Optional<StatutCompte> findTopByCompte_IdCompteOrderByDateStatutDesc(Long idCompte);

	// Liste paginee des statuts appliques entre deux dates.
	Page<StatutCompte> findByDateStatutBetween(LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable);

	// Liste paginee des statuts crees entre deux dates.
	Page<StatutCompte> findByCreatedAtBetween(LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable);
}
