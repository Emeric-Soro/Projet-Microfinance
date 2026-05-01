package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.CompteLiaisonAgence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompteLiaisonAgenceRepository extends JpaRepository<CompteLiaisonAgence, Long> {
    Optional<CompteLiaisonAgence> findByAgenceSource_IdAgenceAndAgenceDestination_IdAgenceAndActifTrue(Long idAgenceSource, Long idAgenceDestination);
    List<CompteLiaisonAgence> findByAgenceSource_IdAgenceOrAgenceDestination_IdAgence(Long idAgenceSource, Long idAgenceDestination);
}

