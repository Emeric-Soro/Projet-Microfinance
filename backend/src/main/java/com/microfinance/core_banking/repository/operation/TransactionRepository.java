package com.microfinance.core_banking.repository.operation;

import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.entity.StatutOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Recherche unique par reference metier de transaction.
    Optional<Transaction> findByReferenceUnique(String referenceUnique);

    // Verification rapide de si une transaction existe par reference.
    boolean existsByReferenceUnique(String referenceUnique);

    // Liste paginee des transactions initiees par un utilisateur.
    Page<Transaction> findByUtilisateur_IdUser(Long idUser, Pageable pageable);

    // Liste paginee des transactions par type.
    Page<Transaction> findByTypeTransaction_IdTypeTransaction(Long idTypeTransaction, Pageable pageable);

    // Liste paginee des transactions selon date et heure d'execution.
    Page<Transaction> findByDateHeureTransactionBetween(LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable);

    // Liste paginee des transactions selon une plage de montant.
    Page<Transaction> findByMontantGlobalBetween(BigDecimal montantMin, BigDecimal montantMax, Pageable pageable);

    // Historique pagine des transactions impactant un numero de compte donne.
    Page<Transaction> findByLignesEcriture_Compte_NumCompte(String numCompte, Pageable pageable);

    // Liste paginee des transactions creees entre deux dates.
    Page<Transaction> findByCreatedAtBetween(LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable);

    long countBySessionCaisse_IdSessionCaisseAndStatutOperation(Long idSessionCaisse, StatutOperation statutOperation);

    long countByStatutOperation(StatutOperation statutOperation);

    List<Transaction> findByDateExecutionBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    List<Transaction> findByDateHeureTransactionBetweenOrderByDateHeureTransactionAsc(LocalDateTime dateDebut, LocalDateTime dateFin);
}
