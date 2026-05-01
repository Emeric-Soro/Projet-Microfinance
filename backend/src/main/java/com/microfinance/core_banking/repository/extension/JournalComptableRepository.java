package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.JournalComptable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JournalComptableRepository extends JpaRepository<JournalComptable, Long> {
    Optional<JournalComptable> findByCodeJournal(String codeJournal);
}
