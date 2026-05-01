package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.SchemaComptable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchemaComptableRepository extends JpaRepository<SchemaComptable, Long> {
    Optional<SchemaComptable> findByCodeOperation(String codeOperation);
}
