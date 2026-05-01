package com.microfinance.core_banking.service.journalentry;

import com.microfinance.core_banking.dto.request.journalentry.JournalEntryRequestDTO;
import com.microfinance.core_banking.dto.response.journalentry.JournalEntryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JournalEntryService {
    JournalEntryResponseDTO create(JournalEntryRequestDTO request);
    JournalEntryResponseDTO getById(Long id);
    Page<JournalEntryResponseDTO> getAll(Pageable pageable);
    JournalEntryResponseDTO update(Long id, JournalEntryRequestDTO request);
    void delete(Long id);
}
