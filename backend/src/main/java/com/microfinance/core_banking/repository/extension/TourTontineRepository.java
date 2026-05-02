package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.TourTontine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourTontineRepository extends JpaRepository<TourTontine, Long> {
    List<TourTontine> findByTontine_IdTontineOrderByNumeroTourAsc(Long idTontine);
    List<TourTontine> findByBeneficiaire_IdClient(Long idClient);
}
