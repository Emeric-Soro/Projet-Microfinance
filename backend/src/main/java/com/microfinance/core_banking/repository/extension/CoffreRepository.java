package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.Coffre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoffreRepository extends JpaRepository<Coffre, Long> {
    Optional<Coffre> findByCodeCoffre(String codeCoffre);
    List<Coffre> findByAgence_IdAgence(Long idAgence);
}
