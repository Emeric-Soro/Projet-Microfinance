package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.CompteComptable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompteComptableRepository extends JpaRepository<CompteComptable, Long> {
    Optional<CompteComptable> findByNumeroCompte(String numeroCompte);
    List<CompteComptable> findByAgence_IdAgenceOrAgenceIsNull(Long idAgence);
}
