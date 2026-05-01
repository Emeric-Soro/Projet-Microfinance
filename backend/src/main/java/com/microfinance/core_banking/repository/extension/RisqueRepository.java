package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.Risque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RisqueRepository extends JpaRepository<Risque, Long> {
    Optional<Risque> findByCodeRisque(String codeRisque);
}
