package com.microfinance.core_banking.repository.credit;

import com.microfinance.core_banking.entity.Echeance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

	// Compte le nombre d'echeances impayees d'un credit.
	long countByCredit_IdCreditAndEstPayeeFalse(Long idCredit);
}
