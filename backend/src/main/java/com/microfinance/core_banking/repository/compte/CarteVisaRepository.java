package com.microfinance.core_banking.repository.compte;

import com.microfinance.core_banking.entity.CarteVisa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CarteVisaRepository extends JpaRepository<CarteVisa, Long> {

	// Recherche unique par numero de carte.
	Optional<CarteVisa> findByNumeroCarte(String numeroCarte);
	// Verification rapide de si une carte existe par numero.
	boolean existsByNumeroCarte(String numeroCarte);

	// Liste paginee des cartes rattachees a un compte.
	Page<CarteVisa> findByCompte_IdCompte(Long idCompte, Pageable pageable);

	// Liste paginee des cartes selon leur statut actif/inactif.
	Page<CarteVisa> findByStatut(Boolean statut, Pageable pageable);

	// Liste paginee des cartes expirant entre deux dates.
	Page<CarteVisa> findByDateExpirationBetween(LocalDate dateDebut, LocalDate dateFin, Pageable pageable);

	// Liste paginee des cartes creees entre deux dates.
	Page<CarteVisa> findByCreatedAtBetween(LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable);
}
