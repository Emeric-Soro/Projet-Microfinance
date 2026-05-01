package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.OrdrePaiementExterne;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdrePaiementExterneRepository extends JpaRepository<OrdrePaiementExterne, Long> {
    Optional<OrdrePaiementExterne> findByReferenceOrdre(String referenceOrdre);
    List<OrdrePaiementExterne> findByStatutIgnoreCase(String statut);
    List<OrdrePaiementExterne> findByStatutIgnoreCaseOrderByDateInitiationAsc(String statut);
}
