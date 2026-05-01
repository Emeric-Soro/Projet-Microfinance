package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.EcritureComptable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EcritureComptableRepository extends JpaRepository<EcritureComptable, Long> {
    Optional<EcritureComptable> findBySourceTypeAndSourceReference(String sourceType, String sourceReference);
    List<EcritureComptable> findByDateComptableBetween(LocalDate dateDebut, LocalDate dateFin);
    long countByDateComptableBetween(LocalDate dateDebut, LocalDate dateFin);
}
