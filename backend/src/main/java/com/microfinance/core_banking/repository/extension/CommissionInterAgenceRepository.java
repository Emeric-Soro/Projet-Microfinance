package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.CommissionInterAgence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommissionInterAgenceRepository extends JpaRepository<CommissionInterAgence, Long> {
    Optional<CommissionInterAgence> findByOperationDeplacee_IdOperationDeplacee(Long idOperationDeplacee);
}

