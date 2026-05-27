package com.soutra.microfinance.mapper;

import com.soutra.microfinance.dto.response.operation.LigneReleveResponseDTO;
import com.soutra.microfinance.dto.response.operation.RecuTransactionResponseDTO;
import com.soutra.microfinance.entity.LigneEcriture;
import com.soutra.microfinance.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OperationMapper {

    @Mapping(source = "typeTransaction.libelle", target = "typeOperation")
    @Mapping(source = "montantGlobal", target = "montant")
    @Mapping(source = "dateHeureTransaction", target = "dateHeure")
    @Mapping(target = "statutOperation", expression = "java(transaction.getStatutOperation() == null ? null : transaction.getStatutOperation().name())")
    RecuTransactionResponseDTO toRecuResponseDTO(Transaction transaction);

    // Pour un relevé de compte, la ligne va chercher les infos dans la Transaction globale
    @Mapping(source = "transaction.dateHeureTransaction", target = "dateOperation")
    @Mapping(source = "transaction.typeTransaction.libelle", target = "libelle")
    LigneReleveResponseDTO toLigneReleveResponseDTO(LigneEcriture ligneEcriture);
}
