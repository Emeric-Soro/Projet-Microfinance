package com.soutra.microfinance.service.operation;

import com.soutra.microfinance.config.TransactionWorkflowProperties;
import com.soutra.microfinance.constant.AppConstants;
import com.soutra.microfinance.entity.Caisse;
import com.soutra.microfinance.entity.CarteVisa;
import com.soutra.microfinance.entity.Compte;
import com.soutra.microfinance.entity.LigneEcriture;
import com.soutra.microfinance.entity.RoleUtilisateur;
import com.soutra.microfinance.entity.SensEcriture;
import com.soutra.microfinance.entity.StatutOperation;
import com.soutra.microfinance.entity.Transaction;
import com.soutra.microfinance.entity.TypeTransaction;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.api.exception.CompteNotFoundException;
import com.soutra.microfinance.api.exception.SoldeInsuffisantException;
import com.soutra.microfinance.api.exception.TransactionWorkflowException;
import com.soutra.microfinance.repository.client.UtilisateurRepository;
import com.soutra.microfinance.repository.compte.CarteVisaRepository;
import com.soutra.microfinance.repository.compte.CompteRepository;
import com.soutra.microfinance.repository.operation.CaisseRepository;
import com.soutra.microfinance.repository.operation.LigneEcritureRepository;
import com.soutra.microfinance.repository.operation.TransactionRepository;
import com.soutra.microfinance.repository.operation.TypeTransactionRepository;
import com.soutra.microfinance.service.communication.event.VirementEffectueEvent;
import com.soutra.microfinance.service.comptabilite.ComptabiliteOperationnelleService;
import com.soutra.microfinance.service.operation.fees.TransactionFeeCalculator;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Set<String> ROLES_SUPERVISION = Set.of(
            AppConstants.ROLE_ADMIN, AppConstants.ROLE_SUPERVISEUR, AppConstants.ROLE_CHEF_AGENCE);

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final LigneEcritureRepository ligneEcritureRepository;
    private final TypeTransactionRepository typeTransactionRepository;
    private final CompteRepository compteRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CarteVisaRepository carteVisaRepository;
    private final CaisseRepository caisseRepository;
    private final TransactionFeeCalculator transactionFeeCalculator;
    private final ApplicationEventPublisher eventPublisher;
    private final TransactionWorkflowProperties transactionWorkflowProperties;
    private final ComptabiliteOperationnelleService comptabiliteOperationnelleService;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            LigneEcritureRepository ligneEcritureRepository,
            TypeTransactionRepository typeTransactionRepository,
            CompteRepository compteRepository,
            UtilisateurRepository utilisateurRepository,
            CarteVisaRepository carteVisaRepository,
            CaisseRepository caisseRepository,
            TransactionFeeCalculator transactionFeeCalculator,
            ApplicationEventPublisher eventPublisher,
            TransactionWorkflowProperties transactionWorkflowProperties,
            ComptabiliteOperationnelleService comptabiliteOperationnelleService
    ) {
        this.transactionRepository = transactionRepository;
        this.ligneEcritureRepository = ligneEcritureRepository;
        this.typeTransactionRepository = typeTransactionRepository;
        this.compteRepository = compteRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.carteVisaRepository = carteVisaRepository;
        this.caisseRepository = caisseRepository;
        this.transactionFeeCalculator = transactionFeeCalculator;
        this.eventPublisher = eventPublisher;
        this.transactionWorkflowProperties = transactionWorkflowProperties;
        this.comptabiliteOperationnelleService = comptabiliteOperationnelleService;
    }

    @Override
    @Transactional
    public Transaction faireDepot(String numCompte, BigDecimal montant, Long idUser) {
        validerMontantPositif(montant);

        Caisse caisse = caisseRepository.findByUtilisateur_IdUserAndStatut(idUser, Caisse.StatutCaisse.OUVERTE)
                .orElseThrow(() -> new IllegalStateException("Vous devez ouvrir une caisse avant d'effectuer un depot"));

        Compte compte = chargerCompte(numCompte);
        Utilisateur utilisateur = chargerUtilisateur(idUser);
        TypeTransaction typeDepot = chargerTypeStrict(AppConstants.TX_DEPOT);
        BigDecimal frais = transactionFeeCalculator.calculerFrais(typeDepot.getCodeTypeTransaction(), montant);
        BigDecimal montantNet = montant.subtract(frais);
        if (montantNet.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Le montant net apres frais doit rester strictement positif");
        }

        Transaction transaction = creerTransaction(
                utilisateur,
                typeDepot,
                montant,
                frais,
                null,
                compte,
                necessiteValidationSuperviseur(montant)
        );

        if (Boolean.TRUE.equals(transaction.getValidationSuperviseurRequise())) {
            return transaction;
        }

        Transaction executed = executerTransaction(transaction);
        mettreAJourCaisse(caisse, montant);
        return executed;
    }

    @Override
    @Transactional
    public Transaction faireRetrait(String numCompte, BigDecimal montant, Long idUser, String numeroCarte) {
        validerMontantPositif(montant);

        // Si le retrait est effectue via une carte VISA
        if (numeroCarte != null && !numeroCarte.isBlank()) {
            CarteVisa carte = carteVisaRepository.findByNumeroCarte(numeroCarte)
                    .orElseThrow(() -> new EntityNotFoundException("Carte VISA introuvable"));
            if (!Boolean.TRUE.equals(carte.getStatut())) {
                throw new IllegalStateException("Cette carte VISA est inactive ou en opposition.");
            }
            if (carte.getDateExpiration().isBefore(LocalDate.now())) {
                throw new IllegalStateException("Cette carte VISA est expiree.");
            }
            if (montant.compareTo(carte.getPlafondJournalier()) > 0) {
                throw new IllegalStateException("Le montant demande depasse le plafond journalier de la carte ("
                        + carte.getPlafondJournalier() + " FCFA).");
            }
        }

        if (montant.compareTo(transactionWorkflowProperties.getMaxRetraitGuichet()) > 0) {
            throw new IllegalStateException(
                    "Le montant du retrait (" + montant + " FCFA) depasse le plafond maximum autorise au guichet ("
                            + transactionWorkflowProperties.getMaxRetraitGuichet() + " FCFA). "
                            + "Veuillez contacter un superviseur."
            );
        }

        Caisse caisse = caisseRepository.findByUtilisateur_IdUserAndStatut(idUser, Caisse.StatutCaisse.OUVERTE)
                .orElseThrow(() -> new IllegalStateException("Vous devez ouvrir une caisse avant d'effectuer un retrait"));

        Compte compte = chargerCompte(numCompte);
        Utilisateur utilisateur = chargerUtilisateur(idUser);
        TypeTransaction typeRetrait = chargerTypeStrict(AppConstants.TX_RETRAIT);
        BigDecimal frais = transactionFeeCalculator.calculerFrais(typeRetrait.getCodeTypeTransaction(), montant);

        Transaction transaction = creerTransaction(
                utilisateur,
                typeRetrait,
                montant,
                frais,
                compte,
                null,
                necessiteValidationSuperviseur(montant)
        );

        if (Boolean.TRUE.equals(transaction.getValidationSuperviseurRequise())) {
            return transaction;
        }

        Transaction executed = executerTransaction(transaction);
        mettreAJourCaisse(caisse, montant.negate());
        return executed;
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
        TypeTransaction typeVirement = chargerTypeStrict(AppConstants.TX_VIREMENT);
        BigDecimal frais = transactionFeeCalculator.calculerFrais(typeVirement.getCodeTypeTransaction(), montant);

        Transaction transaction = creerTransaction(
                utilisateur,
                typeVirement,
                montant,
                frais,
                source,
                destination,
                necessiteValidationSuperviseur(montant)
        );

        if (Boolean.TRUE.equals(transaction.getValidationSuperviseurRequise())) {
            return transaction;
        }

        return executerTransaction(transaction);
    }

    @Override
    @Transactional
    public Transaction fairePaiementCarte(String numeroCarte, BigDecimal montant, Long idUser) {
        validerMontantPositif(montant);

        CarteVisa carte = carteVisaRepository.findByNumeroCarte(numeroCarte)
                .orElseThrow(() -> new EntityNotFoundException("Carte introuvable : " + numeroCarte));

        if (Boolean.FALSE.equals(carte.getStatut())) {
            throw new IllegalStateException("La carte " + numeroCarte + " est inactive");
        }

        if (carte.getDateExpiration() != null && carte.getDateExpiration().isBefore(LocalDate.now())) {
            throw new IllegalStateException("La carte " + numeroCarte + " a expire le " + carte.getDateExpiration());
        }

        if (montant.compareTo(carte.getPlafondJournalier()) > 0) {
            throw new IllegalStateException(
                    "Le montant du paiement (" + montant + " FCFA) depasse le plafond journalier de la carte ("
                            + carte.getPlafondJournalier() + " FCFA)"
            );
        }

        Compte compte = carte.getCompte();
        Utilisateur utilisateur = chargerUtilisateur(idUser);
        TypeTransaction typePaiementCarte = chargerTypeStrict(AppConstants.TX_PAIEMENT_CARTE);
        BigDecimal frais = transactionFeeCalculator.calculerFrais(typePaiementCarte.getCodeTypeTransaction(), montant);

        Transaction transaction = creerTransaction(
                utilisateur,
                typePaiementCarte,
                montant,
                frais,
                compte,
                null,
                necessiteValidationSuperviseur(montant)
        );

        if (Boolean.TRUE.equals(transaction.getValidationSuperviseurRequise())) {
            return transaction;
        }

        return executerTransaction(transaction);
    }

    @Override
    @Transactional
    public Transaction approuverTransaction(String referenceUnique, Long idSuperviseur) {
        Transaction transaction = chargerTransaction(referenceUnique);
        Utilisateur superviseur = chargerUtilisateur(idSuperviseur);

        verifierTransactionEnAttente(transaction);
        verifierSuperviseur(superviseur, transaction.getUtilisateur());

        transaction.setUtilisateurValidation(superviseur);
        transaction.setDateValidation(LocalDateTime.now());
        return executerTransaction(transaction);
    }

    @Override
    @Transactional
    public Transaction rejeterTransaction(String referenceUnique, Long idSuperviseur, String motif) {
        Transaction transaction = chargerTransaction(referenceUnique);
        Utilisateur superviseur = chargerUtilisateur(idSuperviseur);

        verifierTransactionEnAttente(transaction);
        verifierSuperviseur(superviseur, transaction.getUtilisateur());

        transaction.setUtilisateurValidation(superviseur);
        transaction.setDateValidation(LocalDateTime.now());
        transaction.setMotifRejet((motif == null || motif.isBlank()) ? "Rejet superviseur" : motif.trim());
        transaction.setStatutOperation(StatutOperation.REJETEE);
        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LigneEcriture> historiqueOperations(String numCompte, Pageable pageable) {
        Compte compte = chargerCompte(numCompte);
        return ligneEcritureRepository.findByCompte_IdCompte(compte.getIdCompte(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> listerEnAttente(Pageable pageable) {
        return transactionRepository.findByStatutOperation(StatutOperation.EN_ATTENTE, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> listerTransactionsUtilisateur(Long idUser, Pageable pageable) {
        if (!utilisateurRepository.existsById(idUser)) {
            throw new EntityNotFoundException("Utilisateur introuvable: " + idUser);
        }
        return transactionRepository.findByUtilisateur_IdUser(idUser, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Transaction getDetailTransaction(String referenceUnique) {
        return chargerTransaction(referenceUnique);
    }

    @Override
    @Transactional
    public Transaction reverserTransaction(String referenceUnique, Long idUser, String motif) {
        Transaction transaction = chargerTransaction(referenceUnique);
        if (transaction.getStatutOperation() != StatutOperation.EXECUTEE) {
            throw new TransactionWorkflowException("Seules les transactions executees peuvent etre reversees");
        }
        Utilisateur utilisateur = chargerUtilisateur(idUser);

        String codeType = transaction.getTypeTransaction().getCodeTypeTransaction();
        Compte sourceOriginal = rechargerCompteTransaction(transaction.getCompteSource());
        Compte destinationOriginal = rechargerCompteTransaction(transaction.getCompteDestination());

        if (AppConstants.TX_DEPOT.equalsIgnoreCase(codeType) && destinationOriginal != null) {
            comptabiliteOperationnelleService.debiterCompte(destinationOriginal, transaction.getMontantGlobal());
        } else if (AppConstants.TX_RETRAIT.equalsIgnoreCase(codeType) && sourceOriginal != null) {
            comptabiliteOperationnelleService.crediterCompte(sourceOriginal, transaction.getMontantGlobal());
        } else if (AppConstants.TX_VIREMENT.equalsIgnoreCase(codeType)) {
            if (sourceOriginal != null) {
                comptabiliteOperationnelleService.crediterCompte(sourceOriginal, transaction.getMontantGlobal());
            }
            if (destinationOriginal != null) {
                comptabiliteOperationnelleService.debiterCompte(destinationOriginal, transaction.getMontantGlobal());
            }
        }

        transaction.setStatutOperation(StatutOperation.REJETEE);
        transaction.setMotifRejet("REVERSEMENT: " + (motif != null ? motif : "Annulation manuelle"));
        transaction.setUtilisateurValidation(utilisateur);
        transaction.setDateValidation(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public Transaction faireDepotMobileMoney(String numCompte, BigDecimal montant, Long idUser, String operateur, String telephone) {
        validerMontantPositif(montant);
        Compte compte = chargerCompte(numCompte);
        Utilisateur utilisateur = chargerUtilisateur(idUser);
        TypeTransaction typeMM = chargerTypeStrict(AppConstants.TX_DEPOT);
        BigDecimal frais = transactionFeeCalculator.calculerFrais(typeMM.getCodeTypeTransaction(), montant);

        Transaction transaction = creerTransaction(utilisateur, typeMM, montant, frais, null, compte, false);
        Transaction executed = executerTransaction(transaction);
        return executed;
    }

    @Override
    @Transactional
    public Transaction faireRetraitMobileMoney(String numCompte, BigDecimal montant, Long idUser, String operateur, String telephone) {
        validerMontantPositif(montant);
        Compte compte = chargerCompte(numCompte);
        Utilisateur utilisateur = chargerUtilisateur(idUser);
        TypeTransaction typeMM = chargerTypeStrict(AppConstants.TX_RETRAIT);
        BigDecimal frais = transactionFeeCalculator.calculerFrais(typeMM.getCodeTypeTransaction(), montant);

        Transaction transaction = creerTransaction(utilisateur, typeMM, montant, frais, compte, null, false);
        Transaction executed = executerTransaction(transaction);
        return executed;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> listerMobileMoney(Pageable pageable) {
        return transactionRepository.findByTypeTransaction_CodeTypeTransaction("MOBILE_MONEY", pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public String exporterTransactions(String format, LocalDateTime dateDebut, LocalDateTime dateFin) {
        if (!"CSV".equalsIgnoreCase(format)) {
            throw new UnsupportedOperationException("Format \"" + format + "\" non supporte. Utiliser CSV.");
        }

        List<Transaction> transactions;
        if (dateDebut != null && dateFin != null) {
            transactions = transactionRepository
                    .findByDateHeureTransactionBetween(dateDebut, dateFin);
        } else if (dateDebut != null) {
            transactions = transactionRepository
                    .findByDateHeureTransactionAfter(dateDebut);
        } else {
            transactions = transactionRepository.findAll();
        }

        try (StringWriter writer = new StringWriter()) {
            writer.write("Reference;Type;Montant;Frais;Statut;Date;CompteSource;CompteDestination\n");
            for (Transaction tx : transactions) {
                writer.write(String.format("%s;%s;%s;%s;%s;%s;%s;%s\n",
                        tx.getReferenceUnique(),
                        tx.getTypeTransaction() != null ? tx.getTypeTransaction().getLibelle() : "",
                        tx.getMontantGlobal(),
                        tx.getFrais(),
                        tx.getStatutOperation(),
                        tx.getDateHeureTransaction(),
                        tx.getCompteSource() != null ? tx.getCompteSource().getNumCompte() : "",
                        tx.getCompteDestination() != null ? tx.getCompteDestination().getNumCompte() : ""
                ));
            }
            String csv = writer.toString();
            log.info("Export CSV genere avec {} transactions ({} octets)", transactions.size(), csv.getBytes().length);
            return csv;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la generation du CSV", e);
        }
    }

    private void validerMontantPositif(BigDecimal montant) {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif");
        }
    }

    private Compte chargerCompte(String numCompte) {
        return compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new CompteNotFoundException(numCompte));
    }

    private Utilisateur chargerUtilisateur(Long idUser) {
        return utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable : " + idUser));
    }

    private TypeTransaction chargerTypeStrict(String code) {
        return typeTransactionRepository.findByCodeTypeTransaction(code)
                .orElseThrow(() -> new IllegalStateException("Alerte Système : Le type de transaction '" + code + "' n'est pas configuré en base de données."));
    }

    private Transaction chargerTransaction(String referenceUnique) {
        return transactionRepository.findByReferenceUnique(referenceUnique)
                .orElseThrow(() -> new EntityNotFoundException("Transaction introuvable : " + referenceUnique));
    }

    private Transaction creerTransaction(
            Utilisateur utilisateur,
            TypeTransaction type,
            BigDecimal montant,
            BigDecimal frais,
            Compte compteSource,
            Compte compteDestination,
            boolean validationSuperviseurRequise
    ) {
        Transaction transaction = new Transaction();
        transaction.setReferenceUnique("TX-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        transaction.setDateHeureTransaction(LocalDateTime.now());
        transaction.setMontantGlobal(montant);
        transaction.setFrais(frais);
        transaction.setUtilisateur(utilisateur);
        transaction.setTypeTransaction(type);
        transaction.setCompteSource(compteSource);
        transaction.setCompteDestination(compteDestination);
        transaction.setValidationSuperviseurRequise(validationSuperviseurRequise);
        transaction.setStatutOperation(StatutOperation.EN_ATTENTE);
        transaction.setDateValidation(null);
        transaction.setDateExecution(null);
        transaction.setMotifRejet(null);
        return transactionRepository.save(transaction);
    }

    private Transaction executerTransaction(Transaction transaction) {
        if (transaction.getStatutOperation() == StatutOperation.REJETEE) {
            throw new TransactionWorkflowException("Une transaction rejetee ne peut pas etre executee");
        }
        if (transaction.getStatutOperation() == StatutOperation.EXECUTEE) {
            return transaction;
        }

        String codeType = transaction.getTypeTransaction().getCodeTypeTransaction();
        if (AppConstants.TX_DEPOT.equalsIgnoreCase(codeType)) {
            executerDepot(transaction);
        } else if (AppConstants.TX_RETRAIT.equalsIgnoreCase(codeType)) {
            executerRetrait(transaction);
        } else if (AppConstants.TX_VIREMENT.equalsIgnoreCase(codeType)) {
            executerVirement(transaction);
        } else if (AppConstants.TX_PAIEMENT_CARTE.equalsIgnoreCase(codeType)) {
            executerPaiementCarte(transaction);
        } else {
            throw new IllegalStateException("Type d'operation non supporte pour execution : " + codeType);
        }

        transaction.setStatutOperation(StatutOperation.EXECUTEE);
        transaction.setDateExecution(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    private void executerDepot(Transaction transaction) {
        Compte compte = rechargerCompteTransaction(transaction.getCompteDestination());
        BigDecimal montantNet = transaction.getMontantGlobal().subtract(transaction.getFrais());
        if (montantNet.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionWorkflowException("Le montant net apres frais doit rester strictement positif");
        }

        comptabiliteOperationnelleService.crediterCompte(compte, montantNet);

        comptabiliteOperationnelleService.creerLigne(transaction, compte, SensEcriture.CREDIT, transaction.getMontantGlobal());
        if (transaction.getFrais().compareTo(BigDecimal.ZERO) > 0) {
            comptabiliteOperationnelleService.creerLigne(transaction, compte, SensEcriture.DEBIT, transaction.getFrais());
        }
    }

    private void executerRetrait(Transaction transaction) {
        Compte compte = rechargerCompteTransaction(transaction.getCompteSource());
        BigDecimal montantTotal = transaction.getMontantGlobal().add(transaction.getFrais());
        verifierFondsDisponibles(
                compte,
                montantTotal,
                "Solde insuffisant pour le retrait. Fonds disponibles : " + fondsDisponibles(compte)
        );

        comptabiliteOperationnelleService.debiterCompte(compte, montantTotal);

        comptabiliteOperationnelleService.creerLigne(transaction, compte, SensEcriture.DEBIT, transaction.getMontantGlobal());
        if (transaction.getFrais().compareTo(BigDecimal.ZERO) > 0) {
            comptabiliteOperationnelleService.creerLigne(transaction, compte, SensEcriture.DEBIT, transaction.getFrais());
        }
    }

    private void executerVirement(Transaction transaction) {
        Compte source = rechargerCompteTransaction(transaction.getCompteSource());
        Compte destination = rechargerCompteTransaction(transaction.getCompteDestination());
        BigDecimal montantTotalDebite = transaction.getMontantGlobal().add(transaction.getFrais());
        verifierFondsDisponibles(
                source,
                montantTotalDebite,
                "Solde insuffisant pour effectuer le virement. Fonds disponibles : " + fondsDisponibles(source)
        );

        comptabiliteOperationnelleService.debiterCompte(source, montantTotalDebite);
        comptabiliteOperationnelleService.crediterCompte(destination, transaction.getMontantGlobal());

        comptabiliteOperationnelleService.creerLigne(transaction, source, SensEcriture.DEBIT, transaction.getMontantGlobal());
        if (transaction.getFrais().compareTo(BigDecimal.ZERO) > 0) {
            comptabiliteOperationnelleService.creerLigne(transaction, source, SensEcriture.DEBIT, transaction.getFrais());
        }
        comptabiliteOperationnelleService.creerLigne(transaction, destination, SensEcriture.CREDIT, transaction.getMontantGlobal());
        eventPublisher.publishEvent(new VirementEffectueEvent(destination.getNumCompte(), transaction.getMontantGlobal()));
    }

    private Compte rechargerCompteTransaction(Compte compte) {
        if (compte == null || compte.getIdCompte() == null) {
            throw new TransactionWorkflowException("Compte transactionnel absent");
        }
        return compteRepository.findById(compte.getIdCompte())
                .orElseThrow(() -> new CompteNotFoundException(String.valueOf(compte.getIdCompte())));
    }

    private void verifierFondsDisponibles(Compte compte, BigDecimal montantTotal, String messageErreur) {
        BigDecimal disponibles = fondsDisponibles(compte);
        if (disponibles.compareTo(montantTotal) < 0) {
            throw new SoldeInsuffisantException(messageErreur, disponibles, montantTotal);
        }
    }

    private BigDecimal fondsDisponibles(Compte compte) {
        BigDecimal decouvert = compte.getDecouvertAutorise() == null ? BigDecimal.ZERO : compte.getDecouvertAutorise();
        return compte.getSolde().add(decouvert);
    }

    private void verifierTransactionEnAttente(Transaction transaction) {
        if (transaction.getStatutOperation() != StatutOperation.EN_ATTENTE) {
            throw new TransactionWorkflowException("Seules les transactions en attente peuvent etre validees ou rejetees");
        }
        if (!Boolean.TRUE.equals(transaction.getValidationSuperviseurRequise())) {
            throw new TransactionWorkflowException("Cette transaction ne requiert pas de validation superviseur");
        }
    }

    private void verifierSuperviseur(Utilisateur superviseur, Utilisateur initiateur) {
        boolean roleValide = superviseur.getRoles().stream()
                .map(RoleUtilisateur::getCodeRoleUtilisateur)
                .map(String::toUpperCase)
                .anyMatch(ROLES_SUPERVISION::contains);
        if (!roleValide) {
            throw new TransactionWorkflowException("L'utilisateur choisi n'a pas les droits de supervision requis");
        }
        if (initiateur != null && initiateur.getIdUser() != null && initiateur.getIdUser().equals(superviseur.getIdUser())) {
            throw new TransactionWorkflowException("Le workflow 4-eyes interdit qu'un initiateur valide sa propre transaction");
        }
    }

    private boolean necessiteValidationSuperviseur(BigDecimal montant) {
        return montant.compareTo(transactionWorkflowProperties.getApprovalThreshold()) >= 0;
    }

    private void executerPaiementCarte(Transaction transaction) {
        Compte compte = rechargerCompteTransaction(transaction.getCompteSource());
        BigDecimal montantTotal = transaction.getMontantGlobal().add(transaction.getFrais());
        verifierFondsDisponibles(
                compte,
                montantTotal,
                "Solde insuffisant pour le paiement par carte. Fonds disponibles : " + fondsDisponibles(compte)
        );

        comptabiliteOperationnelleService.debiterCompte(compte, montantTotal);

        comptabiliteOperationnelleService.creerLigne(transaction, compte, SensEcriture.DEBIT, transaction.getMontantGlobal());
        if (transaction.getFrais().compareTo(BigDecimal.ZERO) > 0) {
            comptabiliteOperationnelleService.creerLigne(transaction, compte, SensEcriture.DEBIT, transaction.getFrais());
        }
    }

    private void mettreAJourCaisse(Caisse caisse, BigDecimal variation) {
        caisse.setSoldeCourant(caisse.getSoldeCourant().add(variation));
        caisseRepository.save(caisse);
    }

    // ========== DEPOT INITIAL (ouverture de compte) ==========

    @Override
    @Transactional
    public Transaction faireDepotInitial(String numCompte, BigDecimal montant, Long idUser) {
        validerMontantPositif(montant);

        Compte compte = chargerCompte(numCompte);
        if (compte.getSolde() != null && compte.getSolde().compareTo(BigDecimal.ZERO) != 0) {
            throw new TransactionWorkflowException("Le depot initial n'est autorise que sur un compte dont le solde est nul.");
        }
        Utilisateur utilisateur = chargerUtilisateur(idUser);
        TypeTransaction typeDepot = chargerTypeStrict(AppConstants.TX_DEPOT);

        BigDecimal frais = transactionFeeCalculator.calculerFrais(typeDepot.getCodeTypeTransaction(), montant);
        BigDecimal montantNet = montant.subtract(frais);
        if (montantNet.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Le montant net apres frais doit rester strictement positif");
        }

        // Le depot initial n'exige pas de caisse ouverte (agent commercial).
        Transaction transaction = creerTransaction(
                utilisateur,
                typeDepot,
                montant,
                frais,
                null,
                compte,
                necessiteValidationSuperviseur(montant)
        );

        if (Boolean.TRUE.equals(transaction.getValidationSuperviseurRequise())) {
            return transaction;
        }

        return executerTransaction(transaction);
    }
}
