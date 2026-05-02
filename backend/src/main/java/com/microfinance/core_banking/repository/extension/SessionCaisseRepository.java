package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.SessionCaisse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionCaisseRepository extends JpaRepository<SessionCaisse, Long> {
    List<SessionCaisse> findByCaisse_IdCaisseOrderByDateOuvertureDesc(Long idCaisse);
    SessionCaisse findFirstByCaisse_IdCaisseAndStatutIgnoreCaseOrderByDateOuvertureDesc(Long idCaisse, String statut);
    List<SessionCaisse> findByStatutIgnoreCase(String statut);
}
