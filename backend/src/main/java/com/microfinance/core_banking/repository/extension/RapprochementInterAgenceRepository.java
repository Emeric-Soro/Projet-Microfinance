package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.RapprochementInterAgence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RapprochementInterAgenceRepository extends JpaRepository<RapprochementInterAgence, Long> {
    List<RapprochementInterAgence> findByAgenceSource_IdAgenceOrAgenceDestination_IdAgenceOrderByDateRapprochementDesc(Long idAgenceSource, Long idAgenceDestination);
}

