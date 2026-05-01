package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.OperateurMobileMoney;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperateurMobileMoneyRepository extends JpaRepository<OperateurMobileMoney, Long> {
    Optional<OperateurMobileMoney> findByCodeOperateur(String codeOperateur);
}
