package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.PortefeuilleAgent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortefeuilleAgentRepository extends JpaRepository<PortefeuilleAgent, Long> {
    Optional<PortefeuilleAgent> findByAgent_IdAgent(Long idAgent);
}
