package com.microfinance.core_banking.repository.communication;

import com.microfinance.core_banking.entity.StatutEnvoi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutEnvoiRepository extends JpaRepository<StatutEnvoi, Long> {

    // Recherche unique par code de statut d'envoi.
    Optional<StatutEnvoi> findByCodeStatutEnvoi(String codeStatutEnvoi);

    // Verification rapide de si un statut d'envoi existe par code.
    boolean existsByCodeStatutEnvoi(String codeStatutEnvoi);
}
