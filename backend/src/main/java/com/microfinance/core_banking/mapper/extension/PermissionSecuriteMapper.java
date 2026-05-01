package com.microfinance.core_banking.mapper.extension;

import com.microfinance.core_banking.dto.response.extension.PermissionSecuriteResponseDTO;
import com.microfinance.core_banking.entity.PermissionSecurite;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionSecuriteMapper {
    PermissionSecuriteResponseDTO toDto(PermissionSecurite entity);
    List<PermissionSecuriteResponseDTO> toDtoList(List<PermissionSecurite> entities);
}
