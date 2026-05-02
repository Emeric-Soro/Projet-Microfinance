package com.microfinance.core_banking.repository.compte;

import com.microfinance.core_banking.entity.Compte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {

	// Recherche unique par numero de compte.
	Optional<Compte> findByNumCompte(String numCompte);
	// Verification rapide de si un compte existe par numero.
	boolean existsByNumCompte(String numCompte);
	// Verification rapide de si un compte appartient bien a un client donne.
	boolean existsByNumCompteAndClient_IdClient(String numCompte, Long idClient);

	// Recherche d'un compte sous contrainte d'agence.
	Optional<Compte> findByNumCompteAndAgence_IdAgence(String numCompte, Long idAgence);

	// Nombre de comptes deja ouverts pour un client.
	long countByClient_IdClient(Long idClient);

	// Liste paginee des comptes d'un client.
	Page<Compte> findByClient_IdClient(Long idClient, Pageable pageable);

	// Liste non paginee des comptes d'un client.
	List<Compte> findByClient_IdClient(Long idClient);

	// Liste paginee des comptes par type.
	Page<Compte> findByTypeCompte_IdTypeCompte(Long idTypeCompte, Pageable pageable);

	// Liste paginee des comptes par devise.
	Page<Compte> findByDeviseIgnoreCase(String devise, Pageable pageable);

	// Liste paginee des comptes ouverts entre deux dates.
	Page<Compte> findByDateOuvertureBetween(LocalDate dateDebut, LocalDate dateFin, Pageable pageable);

	// Liste paginee des comptes dont le solde est dans une plage.
	Page<Compte> findBySoldeBetween(BigDecimal soldeMin, BigDecimal soldeMax, Pageable pageable);

	// Liste paginee des comptes crees entre deux dates.
	Page<Compte> findByCreatedAtBetween(LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable);
}
