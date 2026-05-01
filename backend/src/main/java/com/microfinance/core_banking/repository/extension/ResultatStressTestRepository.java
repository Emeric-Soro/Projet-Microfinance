package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.ResultatStressTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultatStressTestRepository extends JpaRepository<ResultatStressTest, Long> {
    List<ResultatStressTest> findByStressTest_IdStressTestOrderByCreatedAtDesc(Long idStressTest);
}
