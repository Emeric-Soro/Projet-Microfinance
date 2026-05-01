package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.DelestageCaisse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DelestageCaisseRepository extends JpaRepository<DelestageCaisse, Long> {
    List<DelestageCaisse> findByCaisse_IdCaisseOrderByCreatedAtDesc(Long idCaisse);
}
