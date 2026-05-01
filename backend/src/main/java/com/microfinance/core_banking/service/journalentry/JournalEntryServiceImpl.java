package com.microfinance.core_banking.service.journalentry;

import com.microfinance.core_banking.dto.request.journalentry.JournalEntryRequestDTO;
import com.microfinance.core_banking.dto.response.journalentry.JournalEntryResponseDTO;
import com.microfinance.core_banking.entity.JournalEntry;
import com.microfinance.core_banking.repository.extension.JournalEntryRepository;
import com.microfinance.core_banking.mapper.JournalEntryMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
public class JournalEntryServiceImpl implements JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final JournalEntryMapper journalEntryMapper;

    public JournalEntryServiceImpl(JournalEntryRepository journalEntryRepository,
                                 JournalEntryMapper journalEntryMapper) {
        this.journalEntryRepository = journalEntryRepository;
        this.journalEntryMapper = journalEntryMapper;
    }

    @Override
    @Transactional
    public JournalEntryResponseDTO create(JournalEntryRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("JournalEntry request cannot be null");
        JournalEntry entity = journalEntryMapper.toEntity(request);
        JournalEntry saved = journalEntryRepository.save(entity);
        return journalEntryMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public JournalEntryResponseDTO getById(Long id) {
        JournalEntry entity = journalEntryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("JournalEntry introuvable: " + id));
        return journalEntryMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JournalEntryResponseDTO> getAll(Pageable pageable) {
        Page<JournalEntry> page = journalEntryRepository.findAll(pageable);
        return page.map(journalEntryMapper::toResponse);
    }

    @Override
    @Transactional
    public JournalEntryResponseDTO update(Long id, JournalEntryRequestDTO request) {
        JournalEntry existing = journalEntryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("JournalEntry introuvable: " + id));
        journalEntryMapper.copyToEntity(request, existing);
        JournalEntry saved = journalEntryRepository.save(existing);
        return journalEntryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!journalEntryRepository.existsById(id)) {
            throw new EntityNotFoundException("JournalEntry introuvable: " + id);
        }
        journalEntryRepository.deleteById(id);
    }
}
