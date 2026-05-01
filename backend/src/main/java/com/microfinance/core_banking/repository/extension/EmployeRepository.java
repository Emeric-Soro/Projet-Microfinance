package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.Employe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeRepository extends JpaRepository<Employe, Long> {
}

