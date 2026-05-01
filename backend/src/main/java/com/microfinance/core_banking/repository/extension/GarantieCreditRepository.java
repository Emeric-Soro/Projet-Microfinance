package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.GarantieCredit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GarantieCreditRepository extends JpaRepository<GarantieCredit, Long> {
    List<GarantieCredit> findByCredit_IdCreditOrderByCreatedAtDesc(Long idCredit);
}
