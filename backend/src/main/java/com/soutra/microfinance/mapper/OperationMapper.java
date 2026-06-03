package com.soutra.microfinance.mapper;

import com.soutra.microfinance.dto.response.operation.LigneReleveResponseDTO;
import com.soutra.microfinance.dto.response.operation.RecuTransactionResponseDTO;
import com.soutra.microfinance.dto.response.operation.TransactionDetailResponseDTO;
import com.soutra.microfinance.dto.response.operation.TransactionEnAttenteResponseDTO;
import com.soutra.microfinance.entity.LigneEcriture;
import com.soutra.microfinance.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {})
public interface OperationMapper {

    @Mapping(source = "typeTransaction.libelle", target = "typeOperation")
    @Mapping(source = "montantGlobal", target = "montant")
    @Mapping(source = "dateHeureTransaction", target = "dateHeure")
    @Mapping(target = "statutOperation", expression = "java(transaction.getStatutOperation() == null ? null : transaction.getStatutOperation().name())")
    RecuTransactionResponseDTO toRecuResponseDTO(Transaction transaction);

    @Mapping(source = "transaction.dateHeureTransaction", target = "dateOperation")
    @Mapping(source = "transaction.typeTransaction.libelle", target = "libelle")
    LigneReleveResponseDTO toLigneReleveResponseDTO(LigneEcriture ligneEcriture);

    @Mapping(source = "typeTransaction.libelle", target = "typeOperation")
    @Mapping(source = "montantGlobal", target = "montant")
    @Mapping(source = "dateHeureTransaction", target = "dateHeure")
    @Mapping(source = "compteSource.numCompte", target = "numCompteSource")
    @Mapping(source = "compteDestination.numCompte", target = "numCompteDestination")
    @Mapping(target = "statutOperation", expression = "java(transaction.getStatutOperation() == null ? null : transaction.getStatutOperation().name())")
    @Mapping(target = "clientNom", expression = "java(transaction.getUtilisateur() != null && transaction.getUtilisateur().getClient() != null ? transaction.getUtilisateur().getClient().getPrenom() + \" \" + transaction.getUtilisateur().getClient().getNom() : null)")
    @Mapping(target = "agentNom", expression = "java(transaction.getUtilisateur() != null ? transaction.getUtilisateur().getLogin() : null)")
    @Mapping(target = "validateurNom", expression = "java(transaction.getUtilisateurValidation() != null ? transaction.getUtilisateurValidation().getLogin() : null)")
    @Mapping(target = "montantNet", expression = "java(transaction.getMontantGlobal().subtract(transaction.getFrais() != null ? transaction.getFrais() : java.math.BigDecimal.ZERO))")
    @Mapping(target = "mode", expression = "java(transaction.getTypeTransaction() != null ? transaction.getTypeTransaction().getLibelle() : null)")
    TransactionDetailResponseDTO toDetailResponseDTO(Transaction transaction);

    @Mapping(source = "typeTransaction.libelle", target = "typeOperation")
    @Mapping(source = "montantGlobal", target = "montant")
    @Mapping(source = "dateHeureTransaction", target = "dateCreation")
    @Mapping(source = "compteSource.numCompte", target = "compteSource")
    @Mapping(source = "compteDestination.numCompte", target = "compteDestination")
    @Mapping(target = "statut", expression = "java(transaction.getStatutOperation() == null ? null : transaction.getStatutOperation().name())")
    @Mapping(target = "clientNom", expression = "java(transaction.getUtilisateur() != null && transaction.getUtilisateur().getClient() != null ? transaction.getUtilisateur().getClient().getPrenom() + \" \" + transaction.getUtilisateur().getClient().getNom() : null)")
    @Mapping(target = "demandeurNom", expression = "java(transaction.getUtilisateur() != null ? transaction.getUtilisateur().getLogin() : null)")
    TransactionEnAttenteResponseDTO toEnAttenteResponseDTO(Transaction transaction);
}
