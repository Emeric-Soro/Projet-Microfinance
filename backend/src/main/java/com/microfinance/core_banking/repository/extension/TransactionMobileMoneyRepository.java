package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.TransactionMobileMoney;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionMobileMoneyRepository extends JpaRepository<TransactionMobileMoney, Long> {
    Optional<TransactionMobileMoney> findByReferenceTransaction(String referenceTransaction);
    List<TransactionMobileMoney> findByWalletClient_IdWalletClientOrderByCreatedAtDesc(Long idWalletClient);
    List<TransactionMobileMoney> findByStatutIgnoreCaseOrderByCreatedAtAsc(String statut);
}
