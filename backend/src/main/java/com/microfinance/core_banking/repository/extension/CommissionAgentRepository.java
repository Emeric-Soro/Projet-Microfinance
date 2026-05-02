package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.CommissionAgent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommissionAgentRepository extends JpaRepository<CommissionAgent, Long> {
    List<CommissionAgent> findByAgent_IdAgent(Long idAgent);
    List<CommissionAgent> findByStatut(String statut);
}
