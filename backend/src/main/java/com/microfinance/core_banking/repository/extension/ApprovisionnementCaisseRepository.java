package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.ApprovisionnementCaisse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovisionnementCaisseRepository extends JpaRepository<ApprovisionnementCaisse, Long> {
    List<ApprovisionnementCaisse> findByCaisse_IdCaisseOrderByCreatedAtDesc(Long idCaisse);
}
