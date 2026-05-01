package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.ClotureComptable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ClotureComptableRepository extends JpaRepository<ClotureComptable, Long> {
    List<ClotureComptable> findByDateFinBetweenOrderByDateFinDesc(LocalDate dateDebut, LocalDate dateFin);
}
