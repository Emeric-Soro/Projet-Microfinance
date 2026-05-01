package com.microfinance.core_banking.mapper;

import com.microfinance.core_banking.dto.request.guarantor.GuarantorRequestDTO;
import com.microfinance.core_banking.dto.response.guarantor.GuarantorResponseDTO;
import com.microfinance.core_banking.entity.Guarantor;
import org.springframework.stereotype.Component;

@Component
public class GuarantorMapper {
    public Guarantor toEntity(GuarantorRequestDTO dto) {
        if (dto == null) return null;
        Guarantor g = new Guarantor();
        g.setLoanFacilityId(dto.getLoanFacilityId());
        g.setGuarantorCustomerId(dto.getGuarantorCustomerId());
        g.setGuaranteeAmount(dto.getGuaranteeAmount());
        g.setGuaranteePercentage(dto.getGuaranteePercentage());
        g.setStatus(dto.getStatus());
        return g;
    }

    public GuarantorResponseDTO toResponse(Guarantor entity) {
        if (entity == null) return null;
        GuarantorResponseDTO dto = new GuarantorResponseDTO();
        dto.setId(entity.getId());
        dto.setLoanFacilityId(entity.getLoanFacilityId());
        dto.setGuarantorCustomerId(entity.getGuarantorCustomerId());
        dto.setGuaranteeAmount(entity.getGuaranteeAmount());
        dto.setGuaranteePercentage(entity.getGuaranteePercentage());
        dto.setStatus(entity.getStatus() == null ? null : entity.getStatus().name());
        return dto;
    }

    public void copyToEntity(GuarantorRequestDTO dto, Guarantor entity) {
        if (dto == null || entity == null) return;
        entity.setLoanFacilityId(dto.getLoanFacilityId());
        entity.setGuarantorCustomerId(dto.getGuarantorCustomerId());
        entity.setGuaranteeAmount(dto.getGuaranteeAmount());
        entity.setGuaranteePercentage(dto.getGuaranteePercentage());
        entity.setStatus(dto.getStatus());
    }
}
