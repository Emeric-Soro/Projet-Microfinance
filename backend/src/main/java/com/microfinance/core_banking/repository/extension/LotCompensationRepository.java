package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.LotCompensation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LotCompensationRepository extends JpaRepository<LotCompensation, Long> {
    Optional<LotCompensation> findByReferenceLot(String referenceLot);
}
