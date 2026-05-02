package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.BilletageCaisse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BilletageCaisseRepository extends JpaRepository<BilletageCaisse, Long> {
    List<BilletageCaisse> findBySessionCaisse_IdSessionCaisse(Long idSessionCaisse);
    List<BilletageCaisse> findBySessionCaisse_Caisse_IdCaisseOrderByDateBilletageDesc(Long idCaisse);
}
