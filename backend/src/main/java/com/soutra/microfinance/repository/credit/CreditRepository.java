package com.soutra.microfinance.repository.credit;

import com.soutra.microfinance.entity.Credit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
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

	// --- Aggregation queries for reporting ---

	long countByStatutCredit_CodeStatut(String codeStatut);

	@Query("SELECT COALESCE(SUM(c.montantAccorde), 0) FROM Credit c")
	BigDecimal sumMontantAccorde();

	@Query("SELECT COALESCE(SUM(c.montantRestantDu), 0) FROM Credit c")
	BigDecimal sumMontantRestantDu();

	@Query("SELECT COALESCE(SUM(c.montantAccorde), 0) FROM Credit c WHERE c.dateDecaissement BETWEEN :dateDebut AND :dateFin")
	BigDecimal sumMontantAccordeByDateDecaissementBetween(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

	@Query("SELECT COUNT(c) FROM Credit c WHERE c.dateDecaissement BETWEEN :dateDebut AND :dateFin")
	long countByDateDecaissementBetween(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

	@Query("SELECT COALESCE(SUM(c.montantAccorde), 0) FROM Credit c WHERE c.statutCredit.codeStatut = :codeStatut")
	BigDecimal sumMontantAccordeByStatutCreditCode(@Param("codeStatut") String codeStatut);

	@Query("SELECT COALESCE(SUM(c.montantRestantDu), 0) FROM Credit c WHERE c.statutCredit.codeStatut = :codeStatut")
	BigDecimal sumMontantRestantDuByStatutCreditCode(@Param("codeStatut") String codeStatut);
}
