package com.soutra.microfinance.repository.comptabilite;

import com.soutra.microfinance.entity.comptabilite.TransactionComptable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionComptableRepository extends JpaRepository<TransactionComptable, Long> {

    // Recherche des transactions comptables par plage de dates.
    List<TransactionComptable> findByDateTransactionBetween(LocalDateTime debut, LocalDateTime fin);

    // Recherche par reference operateur.
    List<TransactionComptable> findByReferenceOperateur(String referenceOperateur);
}
