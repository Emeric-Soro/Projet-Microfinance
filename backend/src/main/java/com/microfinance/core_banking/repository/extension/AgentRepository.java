package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Long> {
    Optional<Agent> findByCodeAgent(String codeAgent);
    List<Agent> findByAgenceRattachement_IdAgence(Long idAgence);
    List<Agent> findByStatut(String statut);
}
