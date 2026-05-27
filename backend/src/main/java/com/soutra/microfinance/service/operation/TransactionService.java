package com.soutra.microfinance.service.operation;

import com.soutra.microfinance.entity.LigneEcriture;
import com.soutra.microfinance.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface TransactionService {

    Transaction faireDepot(String numCompte, BigDecimal montant, Long idUser);

    Transaction faireRetrait(String numCompte, BigDecimal montant, Long idUser, String numeroCarte);

    Transaction faireVirement(String compteSource, String compteDest, BigDecimal montant, Long idUser);

    Transaction approuverTransaction(String referenceUnique, Long idSuperviseur);

    Transaction rejeterTransaction(String referenceUnique, Long idSuperviseur, String motif);

    Transaction fairePaiementCarte(String numeroCarte, BigDecimal montant, Long idUser);

    Page<LigneEcriture> historiqueOperations(String numCompte, Pageable pageable);
}
