package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.CotisationTontine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CotisationTontineRepository extends JpaRepository<CotisationTontine, Long> {
    List<CotisationTontine> findByTourTontine_IdTourTontine(Long idTourTontine);
    List<CotisationTontine> findByParticipant_IdClient(Long idClient);
}
