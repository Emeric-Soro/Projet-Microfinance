package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.ImpayeCredit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImpayeCreditRepository extends JpaRepository<ImpayeCredit, Long> {
    List<ImpayeCredit> findByCredit_IdCreditOrderByJoursRetardDesc(Long idCredit);
    List<ImpayeCredit> findByStatutIgnoreCase(String statut);
    Optional<ImpayeCredit> findByEcheanceCredit_IdEcheanceCredit(Long idEcheanceCredit);
    List<ImpayeCredit> findByCredit_IdCreditAndStatutIgnoreCaseOrderByJoursRetardDesc(Long idCredit, String statut);
}
