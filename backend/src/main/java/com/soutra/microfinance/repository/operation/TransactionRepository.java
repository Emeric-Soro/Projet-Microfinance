package com.soutra.microfinance.repository.operation;

import com.soutra.microfinance.entity.StatutOperation;
import com.soutra.microfinance.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Liste des transactions selon date et heure d'execution (export).
    List<Transaction> findByDateHeureTransactionBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    // Liste des transactions apres une date (export).
    List<Transaction> findByDateHeureTransactionAfter(LocalDateTime dateDebut);

    // Liste paginee des transactions selon une plage de montant.
    Page<Transaction> findByMontantGlobalBetween(BigDecimal montantMin, BigDecimal montantMax, Pageable pageable);

    // Historique pagine des transactions impactant un numero de compte donne.
    Page<Transaction> findByLignesEcriture_Compte_NumCompte(String numCompte, Pageable pageable);

    // Liste paginee des transactions creees entre deux dates.
    Page<Transaction> findByCreatedAtBetween(LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable);

    // Liste paginee des transactions en attente d'approbation.
    Page<Transaction> findByStatutOperation(StatutOperation statutOperation, Pageable pageable);

    // Liste paginee des transactions de type Mobile Money.
    Page<Transaction> findByTypeTransaction_CodeTypeTransaction(String codeType, Pageable pageable);

    // --- Aggregation queries for reporting ---

    long countByDateHeureTransactionBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    long countByStatutOperation(StatutOperation statutOperation);

    long countByDateHeureTransactionBetweenAndStatutOperation(LocalDateTime dateDebut, LocalDateTime dateFin, StatutOperation statutOperation);

    @Query("SELECT COALESCE(SUM(t.montantGlobal), 0) FROM Transaction t " +
           "WHERE t.typeTransaction.codeTypeTransaction = :codeType " +
           "AND t.dateHeureTransaction BETWEEN :dateDebut AND :dateFin " +
           "AND t.statutOperation = :statut")
    BigDecimal sumMontantByTypeCodeDateBetweenAndStatut(@Param("codeType") String codeType,
                                                         @Param("dateDebut") LocalDateTime dateDebut,
                                                         @Param("dateFin") LocalDateTime dateFin,
                                                         @Param("statut") StatutOperation statut);

    @Query("SELECT COALESCE(SUM(t.montantGlobal), 0) FROM Transaction t " +
           "LEFT JOIN t.compteSource cs " +
           "LEFT JOIN t.compteDestination cd " +
           "WHERE t.typeTransaction.codeTypeTransaction = :codeType " +
           "AND t.dateHeureTransaction BETWEEN :dateDebut AND :dateFin " +
           "AND t.statutOperation = :statut " +
           "AND (:agenceId IS NULL OR cs.agence.idAgence = :agenceId " +
           "OR cd.agence.idAgence = :agenceId)")
    BigDecimal sumMontantByTypeCodeDateBetweenAndStatutAndAgence(@Param("codeType") String codeType,
                                                                  @Param("dateDebut") LocalDateTime dateDebut,
                                                                  @Param("dateFin") LocalDateTime dateFin,
                                                                  @Param("statut") StatutOperation statut,
                                                                  @Param("agenceId") Long agenceId);

    @Query("SELECT COALESCE(SUM(t.frais), 0) FROM Transaction t " +
           "WHERE t.dateHeureTransaction BETWEEN :dateDebut AND :dateFin " +
           "AND t.statutOperation = :statut")
    BigDecimal sumFraisByDateBetweenAndStatut(@Param("dateDebut") LocalDateTime dateDebut,
                                               @Param("dateFin") LocalDateTime dateFin,
                                               @Param("statut") StatutOperation statut);

    @Query("SELECT COALESCE(SUM(t.frais), 0) FROM Transaction t " +
           "LEFT JOIN t.compteSource cs " +
           "LEFT JOIN t.compteDestination cd " +
           "WHERE t.dateHeureTransaction BETWEEN :dateDebut AND :dateFin " +
           "AND t.statutOperation = :statut " +
           "AND (:agenceId IS NULL OR cs.agence.idAgence = :agenceId " +
           "OR cd.agence.idAgence = :agenceId)")
    BigDecimal sumFraisByDateBetweenAndStatutAndAgence(@Param("dateDebut") LocalDateTime dateDebut,
                                                        @Param("dateFin") LocalDateTime dateFin,
                                                        @Param("statut") StatutOperation statut,
                                                        @Param("agenceId") Long agenceId);

    @Query("SELECT COALESCE(SUM(t.montantGlobal), 0) FROM Transaction t " +
           "WHERE t.dateHeureTransaction BETWEEN :dateDebut AND :dateFin " +
           "AND t.statutOperation = :statut")
    BigDecimal sumMontantGlobalByDateBetweenAndStatut(@Param("dateDebut") LocalDateTime dateDebut,
                                                       @Param("dateFin") LocalDateTime dateFin,
                                                       @Param("statut") StatutOperation statut);

    @Query("SELECT COALESCE(SUM(t.montantGlobal), 0) FROM Transaction t " +
           "LEFT JOIN t.compteSource cs " +
           "LEFT JOIN t.compteDestination cd " +
           "WHERE t.dateHeureTransaction BETWEEN :dateDebut AND :dateFin " +
           "AND t.statutOperation = :statut " +
           "AND (:agenceId IS NULL OR cs.agence.idAgence = :agenceId " +
           "OR cd.agence.idAgence = :agenceId)")
    BigDecimal sumMontantGlobalByDateBetweenAndStatutAndAgence(@Param("dateDebut") LocalDateTime dateDebut,
                                                                @Param("dateFin") LocalDateTime dateFin,
                                                                @Param("statut") StatutOperation statut,
                                                                @Param("agenceId") Long agenceId);
}
