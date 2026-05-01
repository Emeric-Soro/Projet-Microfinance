package com.microfinance.core_banking.repository.extension;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microfinance.core_banking.entity.LoanFacility;

/**
 * Repository for LoanFacility entities.
 */
public interface LoanFacilityRepository extends JpaRepository<LoanFacility, Long> {
}
