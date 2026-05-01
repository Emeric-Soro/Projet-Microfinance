package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.MouvementCoffre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MouvementCoffreRepository extends JpaRepository<MouvementCoffre, Long> {
    List<MouvementCoffre> findByCoffre_IdCoffreOrderByCreatedAtDesc(Long idCoffre);
}
