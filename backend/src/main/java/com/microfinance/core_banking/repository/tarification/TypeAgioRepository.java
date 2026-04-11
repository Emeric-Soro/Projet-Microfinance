package com.microfinance.core_banking.repository.tarification;

import com.microfinance.core_banking.entity.TypeAgio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TypeAgioRepository extends JpaRepository<TypeAgio, Long> {

	// Recherche unique par code de type d'agio.
	Optional<TypeAgio> findByCodeTypeAgio(String codeTypeAgio);
	// Verification rapide de si un type d'agio existe par code.
	boolean existsByCodeTypeAgio(String codeTypeAgio);

}
