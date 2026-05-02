package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.JournalAppelExterne;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface JournalAppelExterneRepository extends JpaRepository<JournalAppelExterne, Long> {
    List<JournalAppelExterne> findByCodePartenaireOrderByDateAppelDesc(String codePartenaire);
    List<JournalAppelExterne> findByDateAppelBetweenOrderByDateAppelDesc(LocalDateTime debut, LocalDateTime fin);
    List<JournalAppelExterne> findByCodePartenaireAndDateAppelBetweenOrderByDateAppelDesc(String codePartenaire, LocalDateTime debut, LocalDateTime fin);
}
