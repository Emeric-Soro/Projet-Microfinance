package com.microfinance.core_banking.service.collateral;

import com.microfinance.core_banking.dto.request.collateral.CollateralRequestDTO;
import com.microfinance.core_banking.dto.response.collateral.CollateralResponseDTO;
import com.microfinance.core_banking.entity.Collateral;
import com.microfinance.core_banking.repository.extension.CollateralRepository;
import com.microfinance.core_banking.mapper.CollateralMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CollateralServiceImpl implements CollateralService {

    private final CollateralRepository collateralRepository;
    private final CollateralMapper collateralMapper;

    public CollateralServiceImpl(CollateralRepository collateralRepository,
                               CollateralMapper collateralMapper) {
        this.collateralRepository = collateralRepository;
        this.collateralMapper = collateralMapper;
    }

    @Override
    @Transactional
    public CollateralResponseDTO create(CollateralRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("Collateral request cannot be null");
        Collateral entity = collateralMapper.toEntity(request);
        Collateral saved = collateralRepository.save(entity);
        return collateralMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CollateralResponseDTO getById(Long id) {
        Collateral entity = collateralRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Collateral introuvable: " + id));
        return collateralMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CollateralResponseDTO> getAll(Pageable pageable) {
        Page<Collateral> page = collateralRepository.findAll(pageable);
        return page.map(collateralMapper::toResponse);
    }

    @Override
    @Transactional
    public CollateralResponseDTO update(Long id, CollateralRequestDTO request) {
        Collateral existing = collateralRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Collateral introuvable: " + id));
        collateralMapper.copyToEntity(request, existing);
        Collateral saved = collateralRepository.save(existing);
        return collateralMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!collateralRepository.existsById(id)) {
            throw new EntityNotFoundException("Collateral introuvable: " + id);
        }
        collateralRepository.deleteById(id);
    }
}
