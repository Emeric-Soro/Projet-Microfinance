package com.microfinance.core_banking.repository.credit;

import com.microfinance.core_banking.entity.StatutCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutCreditRepository extends JpaRepository<StatutCredit, Long> {

	// Recherche un statut par son code (ex: EN_COURS, EN_RETARD).
	Optional<StatutCredit> findByCodeStatut(String codeStatut);
}
