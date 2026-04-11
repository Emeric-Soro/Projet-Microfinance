package com.microfinance.core_banking.repository.communication;

import com.microfinance.core_banking.entity.TypeCanal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeCanalRepository extends JpaRepository<TypeCanal, Long> {

    // Recherche unique par code de canal.
    Optional<TypeCanal> findByCodeCanal(String codeCanal);

    // Verification rapide de si un canal existe par code.
    boolean existsByCodeCanal(String codeCanal);
}
