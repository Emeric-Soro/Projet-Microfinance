package com.microfinance.core_banking.mapper.extension;

import com.microfinance.core_banking.dto.response.extension.SystemAuditLogResponseDTO;
import com.microfinance.core_banking.entity.SystemAuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SystemAuditLogMapper {
    SystemAuditLogResponseDTO toDto(SystemAuditLog entity);
    List<SystemAuditLogResponseDTO> toDtoList(List<SystemAuditLog> entities);
}
