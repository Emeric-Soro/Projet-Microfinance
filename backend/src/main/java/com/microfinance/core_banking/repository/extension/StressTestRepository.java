package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.StressTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StressTestRepository extends JpaRepository<StressTest, Long> {
    Optional<StressTest> findByCodeScenario(String codeScenario);
}
