package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.Agence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgenceRepository extends JpaRepository<Agence, Long> {
    Optional<Agence> findByCodeAgence(String codeAgence);
}

