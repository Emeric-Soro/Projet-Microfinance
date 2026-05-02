package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.Credit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreditRepository extends JpaRepository<Credit, Long> {
    Optional<Credit> findByReferenceCredit(String referenceCredit);
}

