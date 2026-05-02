package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.ResultatScoring;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResultatScoringRepository extends JpaRepository<ResultatScoring, Long> {
    Optional<ResultatScoring> findByDemandeCredit_IdDemandeCredit(Long idDemandeCredit);
}
