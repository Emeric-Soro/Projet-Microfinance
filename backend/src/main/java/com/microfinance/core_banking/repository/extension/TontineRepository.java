package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.Tontine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TontineRepository extends JpaRepository<Tontine, Long> {
    Optional<Tontine> findByCodeTontine(String codeTontine);
    List<Tontine> findByStatut(String statut);
    List<Tontine> findByAgence_IdAgence(Long idAgence);
}
