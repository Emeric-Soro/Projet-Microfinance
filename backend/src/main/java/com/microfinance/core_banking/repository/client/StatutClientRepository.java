package com.microfinance.core_banking.repository.client;

import com.microfinance.core_banking.entity.StatutClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface StatutClientRepository extends JpaRepository<StatutClient, Long> {

	// Recherche d'un statut client par libelle exact (insensible a la casse).
	Optional<StatutClient> findByLibelleStatutIgnoreCase(String libelleStatut);
	// Verification rapide de si un statut client existe par libelle.
	boolean existsByLibelleStatutIgnoreCase(String libelleStatut);
}
