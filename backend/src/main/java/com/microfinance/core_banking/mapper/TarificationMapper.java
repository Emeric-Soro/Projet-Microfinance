package com.microfinance.core_banking.mapper;

import com.microfinance.core_banking.dto.response.tarification.AgioResponseDTO;
import com.microfinance.core_banking.entity.Agio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TarificationMapper {

    @Mapping(source = "typeAgio.libelle", target = "typeFrais")
    AgioResponseDTO toAgioResponseDTO(Agio agio);
}