package com.microfinance.core_banking.service.operation;

import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.LigneEcriture;
import com.microfinance.core_banking.entity.SensEcriture;
import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.entity.TypeTransaction;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.client.UtilisateurRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.operation.LigneEcritureRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import com.microfinance.core_banking.repository.operation.TypeTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final LigneEcritureRepository ligneEcritureRepository;
    private final TypeTransactionRepository typeTransactionRepository;
    private final CompteRepository compteRepository;
    private final UtilisateurRepository utilisateurRepository;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            LigneEcritureRepository ligneEcritureRepository,
            TypeTransactionRepository typeTransactionRepository,
            CompteRepository compteRepository,
            UtilisateurRepository utilisateurRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.ligneEcritureRepository = ligneEcritureRepository;
        this.typeTransactionRepository = typeTransactionRepository;
        this.compteRepository = compteRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    @Transactional
    public Transaction faireDepot(String numCompte, BigDecimal montant, Long idUser) {
        validerMontantPositif(montant);

        Compte compte = chargerCompte(numCompte);
        Utilisateur utilisateur = chargerUtilisateur(idUser);
        TypeTransaction typeDepot = chargerTypeStrict("DEPOT");

        compte.setSolde(compte.getSolde().add(montant));
        compteRepository.save(compte);

        Transaction transaction = creerTransaction(utilisateur, typeDepot, montant, BigDecimal.ZERO);
        creerLigne(transaction, compte, SensEcriture.CREDIT, montant);
        return transaction;
    }

    @Override
    @Transactional
    public Transaction faireRetrait(String numCompte, BigDecimal montant, Long idUser) {
        validerMontantPositif(montant);

        Compte compte = chargerCompte(numCompte);
        Utilisateur utilisateur = chargerUtilisateur(idUser);
        TypeTransaction typeRetrait = chargerTypeStrict("RETRAIT");

        // Règle métier : Vérification du solde + découvert autorisé
        BigDecimal decouvert = compte.getDecouvertAutorise() == null ? BigDecimal.ZERO : compte.getDecouvertAutorise();
        BigDecimal fondsDisponibles = compte.getSolde().add(decouvert);
        if (fondsDisponibles.compareTo(montant) < 0) {
            throw new IllegalStateException("Solde insuffisant pour le retrait. Fonds disponibles : " + fondsDisponibles);
        }

        compte.setSolde(compte.getSolde().subtract(montant));
        compteRepository.save(compte);

        Transaction transaction = creerTransaction(utilisateur, typeRetrait, montant, BigDecimal.ZERO);
        creerLigne(transaction, compte, SensEcriture.DEBIT, montant);
        return transaction;
    }

    @Override
    @Transactional
    public Transaction faireVirement(String compteSource, String compteDest, BigDecimal montant, Long idUser) {
        if (compteSource == null || compteDest == null || compteSource.equals(compteDest)) {
            throw new IllegalArgumentException("Les comptes source et destination doivent être différents");
        }
        validerMontantPositif(montant);

        Compte source = chargerCompte(compteSource);
        Compte destination = chargerCompte(compteDest);
        Utilisateur utilisateur = chargerUtilisateur(idUser);
        TypeTransaction typeVirement = chargerTypeStrict("VIREMENT");

        // Règle métier : Vérification des fonds sur le compte source
        BigDecimal decouvert = source.getDecouvertAutorise() == null ? BigDecimal.ZERO : source.getDecouvertAutorise();
        BigDecimal fondsDisponibles = source.getSolde().add(decouvert);
        if (fondsDisponibles.compareTo(montant) < 0) {
            throw new IllegalStateException("Solde insuffisant pour effectuer le virement");
        }

        // Mouvements de fonds
        source.setSolde(source.getSolde().subtract(montant));
        destination.setSolde(destination.getSolde().add(montant));
        compteRepository.save(source);
        compteRepository.save(destination);

        // Traçabilité (Comptabilité en partie double)
        Transaction transaction = creerTransaction(utilisateur, typeVirement, montant, BigDecimal.ZERO);
        creerLigne(transaction, source, SensEcriture.DEBIT, montant);
        creerLigne(transaction, destination, SensEcriture.CREDIT, montant);

        return transaction;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LigneEcriture> historiqueOperations(String numCompte, Pageable pageable) {
        Compte compte = chargerCompte(numCompte);
        // On renvoie les Lignes d'Ecriture car elles contiennent le sens exact (Débit/Crédit) pour CE compte
        return ligneEcritureRepository.findByCompte_IdCompte(compte.getIdCompte(), pageable);
    }

    // --- MÉTHODES UTILITAIRES PRIVÉES ---

    private void validerMontantPositif(BigDecimal montant) {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif");
        }
    }

    private Compte chargerCompte(String numCompte) {
        return compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable : " + numCompte));
    }

    private Utilisateur chargerUtilisateur(Long idUser) {
        return utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable : " + idUser));
    }

    private TypeTransaction chargerTypeStrict(String code) {
        // Le paramétrage strict : on refuse de créer une donnée métier à la volée !
        return typeTransactionRepository.findByCodeTypeTransaction(code)
                .orElseThrow(() -> new IllegalStateException("Alerte Système : Le type de transaction '" + code + "' n'est pas configuré en base de données."));
    }

    private Transaction creerTransaction(Utilisateur utilisateur, TypeTransaction type, BigDecimal montant, BigDecimal frais) {
        Transaction transaction = new Transaction();
        transaction.setReferenceUnique("TX-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        transaction.setDateHeureTransaction(LocalDateTime.now());
        transaction.setMontantGlobal(montant);
        transaction.setFrais(frais);
        transaction.setUtilisateur(utilisateur);
        transaction.setTypeTransaction(type);
        return transactionRepository.save(transaction);
    }

    private void creerLigne(Transaction transaction, Compte compte, SensEcriture sens, BigDecimal montant) {
        LigneEcriture ligne = new LigneEcriture();
        ligne.setTransaction(transaction);
        ligne.setCompte(compte);
        ligne.setSens(sens);
        ligne.setMontant(montant);
        ligneEcritureRepository.save(ligne);
    }
}