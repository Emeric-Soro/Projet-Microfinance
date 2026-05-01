package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.ClasseComptable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClasseComptableRepository extends JpaRepository<ClasseComptable, Long> {
    Optional<ClasseComptable> findByCodeClasse(String codeClasse);
}
