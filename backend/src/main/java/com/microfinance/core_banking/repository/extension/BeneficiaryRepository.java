package com.microfinance.core_banking.repository.extension;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microfinance.core_banking.entity.Beneficiary;

/**
 * Repository for Beneficiary entities.
 */
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
}
