package com.microfinance.core_banking.repository.extension;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microfinance.core_banking.entity.Collateral;

/**
 * Repository for Collateral entities.
 */
public interface CollateralRepository extends JpaRepository<Collateral, Long> {
}
