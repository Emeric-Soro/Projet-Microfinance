package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.ProvisionCredit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProvisionCreditRepository extends JpaRepository<ProvisionCredit, Long> {
    List<ProvisionCredit> findByDateCalculOrderByMontantProvisionDesc(LocalDate dateCalcul);
    List<ProvisionCredit> findByCredit_IdCreditOrderByDateCalculDesc(Long idCredit);
    Optional<ProvisionCredit> findByCredit_IdCreditAndDateCalcul(Long idCredit, LocalDate dateCalcul);
}
