package com.microfinance.core_banking.repository.extension;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microfinance.core_banking.entity.JournalEntry;

/**
 * Repository for JournalEntry entities.
 */
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
}
