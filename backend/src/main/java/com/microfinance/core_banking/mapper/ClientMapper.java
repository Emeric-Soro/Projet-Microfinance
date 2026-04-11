package com.microfinance.core_banking.mapper;

import com.microfinance.core_banking.dto.request.client.CreationClientRequestDTO;
import com.microfinance.core_banking.dto.response.client.ClientResponseDTO;
import com.microfinance.core_banking.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// componentModel = "spring" permet de pouvoir injecter ce mapper avec un constructeur dans tes futurs Controllers !
@Mapper(componentModel = "spring")
public interface ClientMapper {

    // 1. On dit à MapStruct comment fabriquer "nomComplet" en fusionnant le nom et le prenom
    @Mapping(target = "nomComplet", expression = "java(client.getNom() + \" \" + client.getPrenom())")
    // 2. On lui dit comment extraire le texte simple du statut depuis l'objet complexe StatutClient
    @Mapping(source = "statutClient.libelleStatut", target = "statut")
    ClientResponseDTO toResponseDTO(Client client);

    // MapStruct va copier les champs du DTO vers l'Entité.
    // On ignore les champs techniques car c'est le Service ou la Base de données qui les génèrent !
    @Mapping(target = "idClient", ignore = true)
    @Mapping(target = "codeClient", ignore = true)
    @Mapping(target = "statutClient", ignore = true)
    @Mapping(target = "dateInscription", ignore = true)
    Client toEntity(CreationClientRequestDTO dto);
}