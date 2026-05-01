package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.Immobilisation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImmobilisationRepository extends JpaRepository<Immobilisation, Long> {
    List<Immobilisation> findByAgence_IdAgenceOrderByDateAcquisitionDesc(Long idAgence);
}
