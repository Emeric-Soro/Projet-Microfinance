package com.soutra.microfinance.service.operation;

import com.soutra.microfinance.entity.LigneEcriture;
import com.soutra.microfinance.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionService {

    Transaction faireDepot(String numCompte, BigDecimal montant, Long idUser);

    /**
     * Depot initial realise a l'ouverture d'un compte.
     * Ne necessite pas de caisse ouverte : l'agent commercial ou le guichetier
     * peut declencher ce premier mouvement juste apres la creation du compte.
     */
    Transaction faireDepotInitial(String numCompte, BigDecimal montant, Long idUser);

    Transaction faireRetrait(String numCompte, BigDecimal montant, Long idUser, String numeroCarte);

    Transaction faireVirement(String compteSource, String compteDest, BigDecimal montant, Long idUser);

    Transaction approuverTransaction(String referenceUnique, Long idSuperviseur);

    Transaction rejeterTransaction(String referenceUnique, Long idSuperviseur, String motif);

    Transaction fairePaiementCarte(String numeroCarte, BigDecimal montant, Long idUser);

    Page<LigneEcriture> historiqueOperations(String numCompte, Pageable pageable);

    Page<Transaction> listerEnAttente(Pageable pageable);

    Transaction getDetailTransaction(String referenceUnique);

    Transaction reverserTransaction(String referenceUnique, Long idUser, String motif);

    Transaction faireDepotMobileMoney(String numCompte, BigDecimal montant, Long idUser, String operateur, String telephone);

    Transaction faireRetraitMobileMoney(String numCompte, BigDecimal montant, Long idUser, String operateur, String telephone);

    Page<Transaction> listerMobileMoney(Pageable pageable);

    // Liste les transactions d'un utilisateur.
    Page<Transaction> listerTransactionsUtilisateur(Long idUser, Pageable pageable);

    String exporterTransactions(String format, LocalDateTime dateDebut, LocalDateTime dateFin);
}
