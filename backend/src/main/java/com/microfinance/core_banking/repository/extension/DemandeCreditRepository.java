package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.DemandeCredit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandeCreditRepository extends JpaRepository<DemandeCredit, Long> {
}

