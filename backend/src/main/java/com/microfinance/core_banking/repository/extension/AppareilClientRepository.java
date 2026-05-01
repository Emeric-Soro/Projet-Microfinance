package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.AppareilClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppareilClientRepository extends JpaRepository<AppareilClient, Long> {
    List<AppareilClient> findByClient_IdClient(Long idClient);
}

