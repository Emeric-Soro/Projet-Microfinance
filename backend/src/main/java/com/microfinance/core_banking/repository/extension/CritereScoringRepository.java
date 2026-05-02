package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.CritereScoring;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CritereScoringRepository extends JpaRepository<CritereScoring, Long> {
    Optional<CritereScoring> findByCodeCritere(String codeCritere);
    List<CritereScoring> findByActifTrue();
}
