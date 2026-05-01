package com.microfinance.core_banking.mapper;

import com.microfinance.core_banking.dto.request.beneficiary.BeneficiaryRequestDTO;
import com.microfinance.core_banking.dto.response.beneficiary.BeneficiaryResponseDTO;
import com.microfinance.core_banking.entity.Beneficiary;
import org.springframework.stereotype.Component;

@Component
public class BeneficiaryMapper {
    public Beneficiary toEntity(BeneficiaryRequestDTO dto) {
        if (dto == null) return null;
        Beneficiary b = new Beneficiary();
        b.setLoanFacilityId(dto.getLoanFacilityId());
        b.setBeneficiaryAccount(dto.getBeneficiaryAccount());
        b.setBeneficiaryName(dto.getBeneficiaryName());
        b.setShare(dto.getShare());
        return b;
    }

    public BeneficiaryResponseDTO toResponse(Beneficiary entity) {
        if (entity == null) return null;
        BeneficiaryResponseDTO dto = new BeneficiaryResponseDTO();
        dto.setId(entity.getId());
        dto.setLoanFacilityId(entity.getLoanFacilityId());
        dto.setBeneficiaryAccount(entity.getBeneficiaryAccount());
        dto.setBeneficiaryName(entity.getBeneficiaryName());
        dto.setShare(entity.getShare());
        return dto;
    }

    public void copyToEntity(BeneficiaryRequestDTO dto, Beneficiary entity) {
        if (dto == null || entity == null) return;
        entity.setLoanFacilityId(dto.getLoanFacilityId());
        entity.setBeneficiaryAccount(dto.getBeneficiaryAccount());
        entity.setBeneficiaryName(dto.getBeneficiaryName());
        entity.setShare(dto.getShare());
    }
}
