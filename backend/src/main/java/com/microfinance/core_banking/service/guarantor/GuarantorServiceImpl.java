package com.microfinance.core_banking.service.guarantor;

import com.microfinance.core_banking.dto.request.guarantor.GuarantorRequestDTO;
import com.microfinance.core_banking.dto.response.guarantor.GuarantorResponseDTO;
import com.microfinance.core_banking.entity.Guarantor;
import com.microfinance.core_banking.repository.extension.GuarantorRepository;
import com.microfinance.core_banking.mapper.GuarantorMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GuarantorServiceImpl implements GuarantorService {

    private final GuarantorRepository guarantorRepository;
    private final GuarantorMapper guarantorMapper;

    public GuarantorServiceImpl(GuarantorRepository guarantorRepository,
                              GuarantorMapper guarantorMapper) {
        this.guarantorRepository = guarantorRepository;
        this.guarantorMapper = guarantorMapper;
    }

    @Override
    @Transactional
    public GuarantorResponseDTO create(GuarantorRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("Guarantor request cannot be null");
        Guarantor entity = guarantorMapper.toEntity(request);
        Guarantor saved = guarantorRepository.save(entity);
        return guarantorMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public GuarantorResponseDTO getById(Long id) {
        Guarantor entity = guarantorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Guarantor introuvable: " + id));
        return guarantorMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GuarantorResponseDTO> getAll(Pageable pageable) {
        Page<Guarantor> page = guarantorRepository.findAll(pageable);
        return page.map(guarantorMapper::toResponse);
    }

    @Override
    @Transactional
    public GuarantorResponseDTO update(Long id, GuarantorRequestDTO request) {
        Guarantor existing = guarantorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Guarantor introuvable: " + id));
        guarantorMapper.copyToEntity(request, existing);
        Guarantor saved = guarantorRepository.save(existing);
        return guarantorMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!guarantorRepository.existsById(id)) {
            throw new EntityNotFoundException("Guarantor introuvable: " + id);
        }
        guarantorRepository.deleteById(id);
    }
}
