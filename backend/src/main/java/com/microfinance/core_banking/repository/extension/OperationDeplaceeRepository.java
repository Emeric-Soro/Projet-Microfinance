package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.OperationDeplacee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationDeplaceeRepository extends JpaRepository<OperationDeplacee, Long> {
    List<OperationDeplacee> findByAgenceOperante_IdAgenceOrAgenceOrigine_IdAgenceOrderByDateOperationDesc(Long idAgenceOperante, Long idAgenceOrigine);
}

