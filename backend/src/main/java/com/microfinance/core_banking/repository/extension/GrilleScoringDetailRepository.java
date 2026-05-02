package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.GrilleScoringDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrilleScoringDetailRepository extends JpaRepository<GrilleScoringDetail, Long> {
    List<GrilleScoringDetail> findByGrilleScoring_IdGrilleScoring(Long idGrilleScoring);
}
