package com.microfinance.core_banking.mapper;

import com.microfinance.core_banking.dto.request.journalentry.JournalEntryRequestDTO;
import com.microfinance.core_banking.dto.response.journalentry.JournalEntryResponseDTO;
import com.microfinance.core_banking.entity.JournalEntry;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class JournalEntryMapper {
    public JournalEntry toEntity(JournalEntryRequestDTO dto) {
        if (dto == null) return null;
        JournalEntry e = new JournalEntry();
        e.setEntryDate(dto.getEntryDate() == null ? LocalDateTime.now() : dto.getEntryDate());
        e.setDebitAccountId(dto.getDebitAccountId());
        e.setCreditAccountId(dto.getCreditAccountId());
        e.setAmount(dto.getAmount());
        e.setDescription(dto.getDescription());
        return e;
    }

    public JournalEntryResponseDTO toResponse(JournalEntry entity) {
        if (entity == null) return null;
        JournalEntryResponseDTO dto = new JournalEntryResponseDTO();
        dto.setId(entity.getId());
        dto.setEntryDate(entity.getEntryDate());
        dto.setDebitAccountId(entity.getDebitAccountId());
        dto.setCreditAccountId(entity.getCreditAccountId());
        dto.setAmount(entity.getAmount());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    public void copyToEntity(JournalEntryRequestDTO dto, JournalEntry entity) {
        if (dto == null || entity == null) return;
        entity.setEntryDate(dto.getEntryDate() == null ? LocalDateTime.now() : dto.getEntryDate());
        entity.setDebitAccountId(dto.getDebitAccountId());
        entity.setCreditAccountId(dto.getCreditAccountId());
        entity.setAmount(dto.getAmount());
        entity.setDescription(dto.getDescription());
    }
}
