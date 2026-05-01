package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.IncidentOperationnel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncidentOperationnelRepository extends JpaRepository<IncidentOperationnel, Long> {
    Optional<IncidentOperationnel> findByReferenceIncident(String referenceIncident);
}
