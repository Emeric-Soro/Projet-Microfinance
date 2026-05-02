package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.TauxFiscal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TauxFiscalRepository extends JpaRepository<TauxFiscal, Long> {
    List<TauxFiscal> findByActifTrue();
    Optional<TauxFiscal> findByCodeTaxeAndActifTrue(String codeTaxe);
    List<TauxFiscal> findByTypeOperationAndActifTrue(String typeOperation);
    List<TauxFiscal> findByDateEffetLessThanEqualAndActifTrue(LocalDate date);
}
