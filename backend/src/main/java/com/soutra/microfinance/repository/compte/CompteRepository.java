package com.soutra.microfinance.repository.compte;

import com.soutra.microfinance.entity.Compte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {

	// Recherche unique par numero de compte.
	Optional<Compte> findByNumCompte(String numCompte);
	// Verification rapide de si un compte existe par numero.
	boolean existsByNumCompte(String numCompte);
	// Verification rapide de si un compte appartient bien a un client donne.
	boolean existsByNumCompteAndClient_IdClient(String numCompte, Long idClient);

	// Nombre de comptes deja ouverts pour un client.
	long countByClient_IdClient(Long idClient);

	// Liste paginee des comptes d'un client.
	Page<Compte> findByClient_IdClient(Long idClient, Pageable pageable);

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

	// --- Aggregation queries for reporting ---

	long countByDateOuvertureBetween(LocalDate dateDebut, LocalDate dateFin);

	@Query("SELECT COALESCE(SUM(c.solde), 0) FROM Compte c")
	BigDecimal sumSolde();

	@Query("SELECT COALESCE(SUM(c.solde), 0) FROM Compte c WHERE c.agence.idAgence = :agenceId")
	BigDecimal sumSoldeByAgence(@Param("agenceId") Long agenceId);

	@Query("SELECT COUNT(c) FROM Compte c WHERE c.agence.idAgence = :agenceId AND c.typeCompte.libelle = :typeLibelle")
	long countByAgenceAndTypeLibelle(@Param("agenceId") Long agenceId, @Param("typeLibelle") String typeLibelle);
}
