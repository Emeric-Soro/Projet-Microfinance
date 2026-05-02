package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.GrilleScoring;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GrilleScoringRepository extends JpaRepository<GrilleScoring, Long> {
    Optional<GrilleScoring> findByCodeGrille(String codeGrille);
    List<GrilleScoring> findByActifTrue();
}
