package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.TransactionAgent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionAgentRepository extends JpaRepository<TransactionAgent, Long> {
    List<TransactionAgent> findByAgent_IdAgent(Long idAgent);
    List<TransactionAgent> findByStatut(String statut);
    List<TransactionAgent> findByDateTransactionBetween(LocalDateTime debut, LocalDateTime fin);
}
