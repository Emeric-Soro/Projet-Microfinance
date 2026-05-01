package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.RemboursementCredit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RemboursementCreditRepository extends JpaRepository<RemboursementCredit, Long> {
    List<RemboursementCredit> findByCredit_IdCreditOrderByDatePaiementDesc(Long idCredit);
    Optional<RemboursementCredit> findByReferenceTransaction(String referenceTransaction);
}
