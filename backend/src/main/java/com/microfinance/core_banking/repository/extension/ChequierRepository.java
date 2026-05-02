package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.Chequier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChequierRepository extends JpaRepository<Chequier, Long> {
    Optional<Chequier> findByNumeroChequier(String numeroChequier);
    List<Chequier> findByCompte_IdCompte(Long idCompte);
    List<Chequier> findByStatut(String statut);
}
