package com.microfinance.core_banking.mapper;

import com.microfinance.core_banking.dto.request.loan.LoanFacilityRequestDTO;
import com.microfinance.core_banking.dto.response.loan.LoanFacilityResponseDTO;
import com.microfinance.core_banking.entity.LoanFacility;
import org.springframework.stereotype.Component;

@Component
public class LoanFacilityMapper {

    public LoanFacility toEntity(LoanFacilityRequestDTO dto) {
        if (dto == null) return null;
        LoanFacility e = new LoanFacility();
        e.setCustomerId(dto.getCustomerId());
        e.setProductId(dto.getProductId());
        e.setPrincipalAmount(dto.getPrincipalAmount());
        e.setTermMonths(dto.getTermMonths());
        e.setStartDate(dto.getStartDate());
        e.setEndDate(dto.getEndDate());
        if (dto.getStatus() != null) {
            try {
                e.setStatus(LoanFacility.LoanStatus.valueOf(dto.getStatus()));
            } catch (Exception ignored) {
                // leave as null if invalid
            }
        }
        return e;
    }

    public LoanFacilityResponseDTO toResponse(LoanFacility entity) {
        if (entity == null) return null;
        LoanFacilityResponseDTO dto = new LoanFacilityResponseDTO();
        dto.setId(entity.getId());
        dto.setCustomerId(entity.getCustomerId());
        dto.setProductId(entity.getProductId());
        dto.setPrincipalAmount(entity.getPrincipalAmount());
        dto.setOutstandingBalance(entity.getOutstandingBalance());
        dto.setInterestRate(entity.getInterestRate());
        dto.setTermMonths(entity.getTermMonths());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        return dto;
    }

    public void copyToEntity(LoanFacilityRequestDTO dto, LoanFacility entity) {
        if (dto == null || entity == null) return;
        entity.setCustomerId(dto.getCustomerId());
        entity.setProductId(dto.getProductId());
        entity.setPrincipalAmount(dto.getPrincipalAmount());
        entity.setTermMonths(dto.getTermMonths());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        if (dto.getStatus() != null) {
            try {
                entity.setStatus(LoanFacility.LoanStatus.valueOf(dto.getStatus()));
            } catch (Exception ignored) {
            }
        }
    }
}
