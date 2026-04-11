package com.microfinance.core_banking.repository.operation;

import com.microfinance.core_banking.entity.LigneEcriture;
import com.microfinance.core_banking.entity.SensEcriture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface LigneEcritureRepository extends JpaRepository<LigneEcriture, Long> {

    // Liste paginee des lignes d'une transaction.
    Page<LigneEcriture> findByTransaction_IdTransaction(Long idTransaction, Pageable pageable);

    // Liste paginee des lignes affectant un compte.
    Page<LigneEcriture> findByCompte_IdCompte(Long idCompte, Pageable pageable);

    // Liste paginee des lignes selon leur sens comptable.
    Page<LigneEcriture> findBySens(SensEcriture sens, Pageable pageable);

    // Liste paginee des lignes selon une plage de montant.
    Page<LigneEcriture> findByMontantBetween(BigDecimal montantMin, BigDecimal montantMax, Pageable pageable);

    // Liste paginee des lignes creees entre deux dates.
    Page<LigneEcriture> findByCreatedAtBetween(LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable);
}
