package com.microfinance.core_banking.repository.tarification;

import com.microfinance.core_banking.entity.Agio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AgioRepository extends JpaRepository<Agio, Long> {

	// Recherche d'un agio pour un compte, un type et une date de calcul.
	Optional<Agio> findByCompte_IdCompteAndTypeAgio_IdTypeAgioAndDateCalcul(
			Long idCompte,
			Long idTypeAgio,
			LocalDate dateCalcul
	);

	// Verification rapide de si un agio existe pour un compte, un type et une date.
	boolean existsByCompte_IdCompteAndTypeAgio_IdTypeAgioAndDateCalcul(
			Long idCompte,
			Long idTypeAgio,
			LocalDate dateCalcul
	);

	// Liste paginee des agios d'un compte.
	Page<Agio> findByCompte_IdCompte(Long idCompte, Pageable pageable);

	// Liste paginee des agios par type.
	Page<Agio> findByTypeAgio_IdTypeAgio(Long idTypeAgio, Pageable pageable);

	// Liste paginee des agios selon l'etat de prelevement.
	Page<Agio> findByEstPreleve(Boolean estPreleve, Pageable pageable);

	// Liste paginee des agios selon une plage de date de calcul.
	Page<Agio> findByDateCalculBetween(LocalDate dateDebut, LocalDate dateFin, Pageable pageable);

	// Liste paginee des agios selon une plage de montant.
	Page<Agio> findByMontantBetween(BigDecimal montantMin, BigDecimal montantMax, Pageable pageable);

	// Liste paginee des agios crees entre deux dates.
	Page<Agio> findByCreatedAtBetween(LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable);
}
