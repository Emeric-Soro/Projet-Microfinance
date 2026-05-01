package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.EcheanceCredit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EcheanceCreditRepository extends JpaRepository<EcheanceCredit, Long> {
    List<EcheanceCredit> findByCredit_IdCreditOrderByNumeroEcheanceAsc(Long idCredit);
    List<EcheanceCredit> findByCredit_IdCreditOrderByDateEcheanceAsc(Long idCredit);
}
