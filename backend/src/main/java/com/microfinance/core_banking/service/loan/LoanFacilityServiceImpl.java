package com.microfinance.core_banking.service.loan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.loan.LoanFacilityRequestDTO;
import com.microfinance.core_banking.dto.response.loan.LoanFacilityResponseDTO;
import com.microfinance.core_banking.entity.LoanFacility;
import com.microfinance.core_banking.repository.extension.LoanFacilityRepository;
import com.microfinance.core_banking.mapper.LoanFacilityMapper;
import com.microfinance.core_banking.service.extension.ValidationExtensionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoanFacilityServiceImpl implements LoanFacilityService {

    private final LoanFacilityRepository loanFacilityRepository;
    private final LoanFacilityMapper loanFacilityMapper;
    private final ValidationExtensionService validationExtensionService;
    private final ObjectMapper objectMapper;

    public LoanFacilityServiceImpl(LoanFacilityRepository loanFacilityRepository,
                                 LoanFacilityMapper loanFacilityMapper,
                                 ValidationExtensionService validationExtensionService,
                                 ObjectMapper objectMapper) {
        this.loanFacilityRepository = loanFacilityRepository;
        this.loanFacilityMapper = loanFacilityMapper;
        this.validationExtensionService = validationExtensionService;
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
    }

    @Override
    @Transactional
    public LoanFacilityResponseDTO create(LoanFacilityRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("La demande de loan facility ne peut pas etre null");
        }
        LoanFacility entity = loanFacilityMapper.toEntity(request);
        // Maker-Checker integration: route large loans through Maker-Checker workflow
        final BigDecimal MAKER_CHECKER_THRESHOLD = new BigDecimal("100000"); // configurable in future
        if (entity.getPrincipalAmount() != null
                && entity.getPrincipalAmount().compareTo(MAKER_CHECKER_THRESHOLD) >= 0
                && validationExtensionService != null) {
            // Persist a placeholder loan with status = APPLICATION_PENDING
            entity.setStatus(LoanFacility.LoanStatus.APPLICATION_PENDING);
            LoanFacility savedPending;
            // Save placeholder to obtain an id for the action reference
            savedPending = loanFacilityRepository.save(entity);

            // Build payload for Maker-Checker action to create this loan facility
            Map<String, Object> payload = new HashMap<>();
            payload.put("typeAction", "CREATE_LOAN_FACILITY");
            payload.put("ressource", "LoanFacility");
            payload.put("referenceRessource", String.valueOf(savedPending.getId()));
            payload.put("ancienneValeur", null);
            try {
                // Serialize the intended final LoanFacility data (without id is fine)
                entity.setId(null);
                String finale = objectMapper.writeValueAsString(entity);
                payload.put("nouvelleValeur", finale);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Impossible de sérialiser la nouvelle valeur du LoanFacility", e);
            }
            payload.put("commentaireMaker", "Création via Maker-Checker");
            // Create the Maker-Checker action
            validationExtensionService.creerAction(payload);
            // Return the placeholder loan with status indicating waiting validation
            return loanFacilityMapper.toResponse(savedPending);
        }
        // Default: persist immediately when below threshold or if Maker-Checker not wired
        entity.setStatus(LoanFacility.LoanStatus.APPLICATION_PENDING);
        LoanFacility saved = loanFacilityRepository.save(entity);
        return loanFacilityMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LoanFacilityResponseDTO getById(Long id) {
        LoanFacility entity = loanFacilityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LoanFacility introuvable: " + id));
        return loanFacilityMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoanFacilityResponseDTO> getAll(Pageable pageable) {
        Page<LoanFacility> page = loanFacilityRepository.findAll(pageable);
        return page.map(loanFacilityMapper::toResponse);
    }

    @Override
    @Transactional
    public LoanFacilityResponseDTO update(Long id, LoanFacilityRequestDTO request) {
        LoanFacility existing = loanFacilityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LoanFacility introuvable: " + id));
        loanFacilityMapper.copyToEntity(request, existing);
        LoanFacility saved = loanFacilityRepository.save(existing);
        return loanFacilityMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!loanFacilityRepository.existsById(id)) {
            throw new EntityNotFoundException("LoanFacility introuvable: " + id);
        }
        loanFacilityRepository.deleteById(id);
    }
}
