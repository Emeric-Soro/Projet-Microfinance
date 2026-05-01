package com.microfinance.core_banking.mapper.extension;

import com.microfinance.core_banking.dto.response.extension.PendingActionResponseDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PendingActionMapper {

    @Mapping(target = "idMaker", source = "maker.idUser")
    @Mapping(target = "idChecker", source = "checker.idUser")
    PendingActionResponseDTO toDto(ActionEnAttente entity);
}
