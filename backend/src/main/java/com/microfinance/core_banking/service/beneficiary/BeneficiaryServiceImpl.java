package com.microfinance.core_banking.service.beneficiary;

import com.microfinance.core_banking.dto.request.beneficiary.BeneficiaryRequestDTO;
import com.microfinance.core_banking.dto.response.beneficiary.BeneficiaryResponseDTO;
import com.microfinance.core_banking.entity.Beneficiary;
import com.microfinance.core_banking.repository.extension.BeneficiaryRepository;
import com.microfinance.core_banking.mapper.BeneficiaryMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final BeneficiaryMapper beneficiaryMapper;

    public BeneficiaryServiceImpl(BeneficiaryRepository beneficiaryRepository,
                                BeneficiaryMapper beneficiaryMapper) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.beneficiaryMapper = beneficiaryMapper;
    }

    @Override
    @Transactional
    public BeneficiaryResponseDTO create(BeneficiaryRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("Beneficiary request cannot be null");
        Beneficiary entity = beneficiaryMapper.toEntity(request);
        Beneficiary saved = beneficiaryRepository.save(entity);
        return beneficiaryMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BeneficiaryResponseDTO getById(Long id) {
        Beneficiary entity = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Beneficiary introuvable: " + id));
        return beneficiaryMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BeneficiaryResponseDTO> getAll(Pageable pageable) {
        Page<Beneficiary> page = beneficiaryRepository.findAll(pageable);
        return page.map(beneficiaryMapper::toResponse);
    }

    @Override
    @Transactional
    public BeneficiaryResponseDTO update(Long id, BeneficiaryRequestDTO request) {
        Beneficiary existing = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Beneficiary introuvable: " + id));
        beneficiaryMapper.copyToEntity(request, existing);
        Beneficiary saved = beneficiaryRepository.save(existing);
        return beneficiaryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!beneficiaryRepository.existsById(id)) {
            throw new EntityNotFoundException("Beneficiary introuvable: " + id);
        }
        beneficiaryRepository.deleteById(id);
    }
}
