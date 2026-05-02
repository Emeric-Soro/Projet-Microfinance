package com.microfinance.core_banking.service.operation;

import com.microfinance.core_banking.entity.LigneEcriture;
import com.microfinance.core_banking.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface TransactionService {

    Transaction faireDepot(String numCompte, BigDecimal montant, Long idUser, Long idSessionCaisse);

    Transaction faireRetrait(String numCompte, BigDecimal montant, Long idUser, Long idSessionCaisse);

    Transaction faireVirement(String compteSource, String compteDest, BigDecimal montant, Long idUser);

    Transaction posterDepotSysteme(String numCompte, BigDecimal montant, BigDecimal frais, Long idUser, String referenceExterne, String codeOperationMetier);

    Transaction posterRetraitSysteme(String numCompte, BigDecimal montant, BigDecimal frais, Long idUser, String referenceExterne, String codeOperationMetier);

    Transaction approuverTransaction(String referenceUnique, Long idSuperviseur);

    Transaction rejeterTransaction(String referenceUnique, Long idSuperviseur, String motif);

    Transaction annulerTransaction(String referenceUnique, Long idSuperviseur, String motif);

    Transaction extournerTransaction(String referenceUnique, Long idSuperviseur, String motif);

    Page<LigneEcriture> historiqueOperations(String numCompte, Pageable pageable);
}
