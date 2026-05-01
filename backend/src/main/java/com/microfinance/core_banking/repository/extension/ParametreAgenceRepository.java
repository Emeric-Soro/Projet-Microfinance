package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.ParametreAgence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParametreAgenceRepository extends JpaRepository<ParametreAgence, Long> {
    List<ParametreAgence> findByAgence_IdAgenceOrderByCodeParametreAscDateEffetDesc(Long idAgence);
}

