package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.ListePep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListePepRepository extends JpaRepository<ListePep, Long> {
    List<ListePep> findByActifTrue();
    List<ListePep> findByNomCompletContainingIgnoreCase(String nom);
    List<ListePep> findByPays(String pays);
    List<ListePep> findByNiveauRisque(String niveauRisque);
    boolean existsByNomCompletIgnoreCase(String nomComplet);
}
