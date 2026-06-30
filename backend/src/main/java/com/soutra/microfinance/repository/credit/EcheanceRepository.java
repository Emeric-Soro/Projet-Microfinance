package com.soutra.microfinance.repository.credit;

import com.soutra.microfinance.entity.Echeance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EcheanceRepository extends JpaRepository<Echeance, Long> {

	// Tableau d'amortissement complet d'un credit, trie par numero.
	List<Echeance> findByCredit_IdCreditOrderByNumeroEcheanceAsc(Long idCredit);

	// Echeances impayees d'un credit (pour le remboursement).
	List<Echeance> findByCredit_IdCreditAndEstPayeeFalseOrderByNumeroEcheanceAsc(Long idCredit);

	// Echeances en retard (batch de detection des impayes).
	List<Echeance> findByDateEcheanceBeforeAndEstPayeeFalse(LocalDate date);

	// Echeances impayees pour une date precise.
	List<Echeance> findByDateEcheanceAndEstPayeeFalse(LocalDate date);

	// Compte le nombre d'echeances impayees d'un credit.
	long countByCredit_IdCreditAndEstPayeeFalse(Long idCredit);

	// --- Aggregation queries for PAR reporting ---

	@Query("SELECT COALESCE(SUM(e.montantTotal - e.montantPaye), 0) FROM Echeance e " +
	       "WHERE e.estPayee = false AND e.dateEcheance < :dateSeuil")
	BigDecimal sumImpayesBeforeDate(@Param("dateSeuil") LocalDate dateSeuil);

	@Query("SELECT COALESCE(SUM(e.montantCapital), 0) FROM Echeance e " +
	       "WHERE e.estPayee = false AND e.dateEcheance < :dateSeuil")
	BigDecimal sumCapitalImpayesBeforeDate(@Param("dateSeuil") LocalDate dateSeuil);
}
