package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.RemiseCheque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RemiseChequeRepository extends JpaRepository<RemiseCheque, Long> {
    List<RemiseCheque> findByChequier_IdChequier(Long idChequier);
    List<RemiseCheque> findByCompteRemise_IdCompte(Long idCompte);
}
