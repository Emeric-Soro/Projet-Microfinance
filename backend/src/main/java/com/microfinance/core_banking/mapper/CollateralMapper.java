package com.microfinance.core_banking.mapper;

import com.microfinance.core_banking.dto.request.collateral.CollateralRequestDTO;
import com.microfinance.core_banking.dto.response.collateral.CollateralResponseDTO;
import com.microfinance.core_banking.entity.Collateral;
import org.springframework.stereotype.Component;

@Component
public class CollateralMapper {

    public Collateral toEntity(CollateralRequestDTO dto) {
        if (dto == null) return null;
        Collateral c = new Collateral();
        c.setLoanFacilityId(dto.getLoanFacilityId());
        c.setCollateralType(dto.getCollateralType());
        c.setDescription(dto.getDescription());
        c.setValue(dto.getValue());
        c.setLienStatus(dto.getLienStatus());
        return c;
    }

    public CollateralResponseDTO toResponse(Collateral entity) {
        if (entity == null) return null;
        CollateralResponseDTO dto = new CollateralResponseDTO();
        dto.setId(entity.getId());
        dto.setLoanFacilityId(entity.getLoanFacilityId());
        dto.setCollateralType(entity.getCollateralType() == null ? null : entity.getCollateralType().name());
        dto.setDescription(entity.getDescription());
        dto.setValue(entity.getValue());
        dto.setLienStatus(entity.getLienStatus() == null ? null : entity.getLienStatus().name());
        return dto;
    }

    public void copyToEntity(CollateralRequestDTO dto, Collateral entity) {
        if (dto == null || entity == null) return;
        entity.setLoanFacilityId(dto.getLoanFacilityId());
        entity.setCollateralType(dto.getCollateralType());
        entity.setDescription(dto.getDescription());
        entity.setValue(dto.getValue());
        entity.setLienStatus(dto.getLienStatus());
    }
}
