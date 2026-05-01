package com.microfinance.core_banking.mapper;

import com.microfinance.core_banking.dto.request.transaction.TransactionRequestDTO;
import com.microfinance.core_banking.dto.response.transaction.TransactionResponseDTO;
import com.microfinance.core_banking.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "idTransaction", target = "id")
    @Mapping(target = "accountId", expression = "java(transaction.getCompteSource() != null ? transaction.getCompteSource().getIdCompte() : transaction.getCompteDestination() != null ? transaction.getCompteDestination().getIdCompte() : null)")
    @Mapping(target = "loanFacilityId", ignore = true)
    @Mapping(source = "montantGlobal", target = "amount")
    @Mapping(target = "currency", constant = "XOF")
    @Mapping(source = "dateHeureTransaction", target = "timestamp")
    @Mapping(target = "type", expression = "java(transaction.getTypeTransaction() == null ? null : transaction.getTypeTransaction().getCodeTypeTransaction())")
    @Mapping(source = "codeOperationMetier", target = "description")
    TransactionResponseDTO toTransactionResponseDTO(Transaction transaction);

    @Mapping(target = "idTransaction", ignore = true)
    @Mapping(target = "referenceUnique", ignore = true)
    @Mapping(target = "dateHeureTransaction", source = "timestamp")
    @Mapping(target = "montantGlobal", source = "amount")
    @Mapping(target = "frais", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "statutOperation", ignore = true)
    @Mapping(target = "validationSuperviseurRequise", ignore = true)
    @Mapping(target = "dateValidation", ignore = true)
    @Mapping(target = "dateExecution", ignore = true)
    @Mapping(target = "motifRejet", ignore = true)
    @Mapping(target = "codeOperationMetier", source = "description")
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "utilisateurValidation", ignore = true)
    @Mapping(target = "typeTransaction", ignore = true)
    @Mapping(target = "compteSource", ignore = true)
    @Mapping(target = "compteDestination", ignore = true)
    @Mapping(target = "sessionCaisse", ignore = true)
    @Mapping(target = "agenceOperation", ignore = true)
    @Mapping(target = "lignesEcriture", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Transaction toEntity(TransactionRequestDTO dto);
}
