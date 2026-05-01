package com.microfinance.core_banking.repository.credit;

import com.microfinance.core_banking.entity.Credit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {

	// Recherche un credit par sa reference unique.
	Optional<Credit> findByReferenceCredit(String referenceCredit);

	// Liste les credits d'un client.
	Page<Credit> findByClient_IdClient(Long idClient, Pageable pageable);

	// Liste les credits filtres par statut.
	Page<Credit> findByStatutCredit_CodeStatut(String codeStatut, Pageable pageable);

	// Verifie si une reference existe deja.
	boolean existsByReferenceCredit(String referenceCredit);
}
