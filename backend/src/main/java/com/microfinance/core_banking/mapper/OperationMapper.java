package com.microfinance.core_banking.mapper;

import com.microfinance.core_banking.dto.response.operation.LigneReleveResponseDTO;
import com.microfinance.core_banking.dto.response.operation.RecuTransactionResponseDTO;
import com.microfinance.core_banking.entity.LigneEcriture;
import com.microfinance.core_banking.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OperationMapper {

    @Mapping(source = "typeTransaction.libelle", target = "typeOperation")
    RecuTransactionResponseDTO toRecuResponseDTO(Transaction transaction);

    // Pour un relevé de compte, la ligne va chercher les infos dans la Transaction globale
    @Mapping(source = "transaction.dateHeureTransaction", target = "dateOperation")
    @Mapping(source = "transaction.typeTransaction.libelle", target = "libelle")
    LigneReleveResponseDTO toLigneReleveResponseDTO(LigneEcriture ligneEcriture);
}