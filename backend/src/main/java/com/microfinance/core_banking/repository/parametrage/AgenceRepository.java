package com.microfinance.core_banking.repository.parametrage;

import com.microfinance.core_banking.entity.Agence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgenceRepository extends JpaRepository<Agence, Long> {

	// Recherche une agence par son code unique.
	Optional<Agence> findByCodeAgence(String codeAgence);

	// Liste les agences actives.
	List<Agence> findByEstActiveTrue();

	// Verifie si un code agence existe deja.
	boolean existsByCodeAgence(String codeAgence);
}
