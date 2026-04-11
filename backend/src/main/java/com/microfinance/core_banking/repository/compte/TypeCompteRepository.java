package com.microfinance.core_banking.repository.compte;

import com.microfinance.core_banking.entity.TypeCompte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TypeCompteRepository extends JpaRepository<TypeCompte, Long> {

	// Recherche unique par libelle de type de compte (insensible a la casse).
	Optional<TypeCompte> findByLibelleIgnoreCase(String libelle);
	// Verification rapide de si un type de compte existe par libelle.
	boolean existsByLibelleIgnoreCase(String libelle);

}
