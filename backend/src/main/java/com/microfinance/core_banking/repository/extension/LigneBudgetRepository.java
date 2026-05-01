package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.LigneBudget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LigneBudgetRepository extends JpaRepository<LigneBudget, Long> {
    List<LigneBudget> findByBudget_IdBudgetOrderByRubriqueAsc(Long idBudget);
}
