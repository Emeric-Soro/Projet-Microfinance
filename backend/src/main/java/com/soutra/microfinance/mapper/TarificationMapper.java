package com.soutra.microfinance.mapper;

import com.soutra.microfinance.dto.response.tarification.AgioResponseDTO;
import com.soutra.microfinance.entity.Agio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TarificationMapper {

    @Mapping(source = "typeAgio.libelle", target = "typeFrais")
    AgioResponseDTO toAgioResponseDTO(Agio agio);
}