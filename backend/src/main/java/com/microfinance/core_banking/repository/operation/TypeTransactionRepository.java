package com.microfinance.core_banking.repository.operation;

import com.microfinance.core_banking.entity.TypeTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TypeTransactionRepository extends JpaRepository<TypeTransaction, Long> {

    // Recherche unique (ex: pour trouver automatiquement le type "VIREMENT" lors d'un transfert)
    Optional<TypeTransaction> findByCodeTypeTransaction(String codeTypeTransaction);

    // Verification rapide de si un type de transaction existe par code.
    boolean existsByCodeTypeTransaction(String codeTypeTransaction);

}
