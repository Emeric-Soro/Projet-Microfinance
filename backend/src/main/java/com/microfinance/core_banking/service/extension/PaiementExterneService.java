package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.ChangerStatutOrdreServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.ChangerStatutTransactionMobileMoneyRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerLotCompensationServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerOperateurServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerOrdrePaiementServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerTransactionMobileMoneyServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerWalletServiceRequestDTO;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.LotCompensation;
import com.microfinance.core_banking.entity.OperateurMobileMoney;
import com.microfinance.core_banking.entity.OrdrePaiementExterne;
import com.microfinance.core_banking.entity.TransactionMobileMoney;
import com.microfinance.core_banking.entity.WalletClient;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.extension.LotCompensationRepository;
import com.microfinance.core_banking.repository.extension.OperateurMobileMoneyRepository;
import com.microfinance.core_banking.repository.extension.OrdrePaiementExterneRepository;
import com.microfinance.core_banking.repository.extension.TransactionMobileMoneyRepository;
import com.microfinance.core_banking.repository.extension.WalletClientRepository;
import com.microfinance.core_banking.service.operation.TransactionService;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaiementExterneService {

    private final OperateurMobileMoneyRepository operateurMobileMoneyRepository;
    private final WalletClientRepository walletClientRepository;
    private final TransactionMobileMoneyRepository transactionMobileMoneyRepository;
    private final LotCompensationRepository lotCompensationRepository;
    private final OrdrePaiementExterneRepository ordrePaiementExterneRepository;
    private final ClientRepository clientRepository;
    private final CompteRepository compteRepository;
    private final TransactionService transactionService;
    private final AuthenticatedUserService authenticatedUserService;

    public PaiementExterneService(
            OperateurMobileMoneyRepository operateurMobileMoneyRepository,
            WalletClientRepository walletClientRepository,
            TransactionMobileMoneyRepository transactionMobileMoneyRepository,
            LotCompensationRepository lotCompensationRepository,
            OrdrePaiementExterneRepository ordrePaiementExterneRepository,
            ClientRepository clientRepository,
            CompteRepository compteRepository,
            TransactionService transactionService,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.operateurMobileMoneyRepository = operateurMobileMoneyRepository;
        this.walletClientRepository = walletClientRepository;
        this.transactionMobileMoneyRepository = transactionMobileMoneyRepository;
        this.lotCompensationRepository = lotCompensationRepository;
        this.ordrePaiementExterneRepository = ordrePaiementExterneRepository;
        this.clientRepository = clientRepository;
        this.compteRepository = compteRepository;
        this.transactionService = transactionService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional
    public OperateurMobileMoney creerOperateur(CreerOperateurServiceRequestDTO dto) {
        OperateurMobileMoney operateur = new OperateurMobileMoney();
        operateur.setCodeOperateur(dto.getCodeOperateur());
        operateur.setNomOperateur(dto.getNomOperateur());
        operateur.setStatut(dto.getStatut());
        return operateurMobileMoneyRepository.save(operateur);
    }

    @Transactional
    public WalletClient creerWallet(CreerWalletServiceRequestDTO dto) {
        Client client = clientRepository.findById(Long.valueOf(dto.getIdClient()))
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));
        verifierPerimetreClient(client);
        OperateurMobileMoney operateur = operateurMobileMoneyRepository.findById(Long.valueOf(dto.getIdOperateurMobileMoney()))
                .orElseThrow(() -> new EntityNotFoundException("Operateur mobile money introuvable"));
        Compte compte = compteRepository.findById(Long.valueOf(dto.getIdCompte()))
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable"));
        if (compte.getClient() == null || !compte.getClient().getIdClient().equals(client.getIdClient())) {
            throw new IllegalStateException("Le compte support du wallet doit appartenir au client");
        }
        WalletClient walletClient = new WalletClient();
        walletClient.setClient(client);
        walletClient.setOperateurMobileMoney(operateur);
        walletClient.setCompte(compte);
        walletClient.setNumeroWallet(dto.getNumeroWallet());
        walletClient.setStatut(dto.getStatut());
        return walletClientRepository.save(walletClient);
    }

    @Transactional
    public TransactionMobileMoney enregistrerTransactionMobileMoney(CreerTransactionMobileMoneyServiceRequestDTO dto) {
        String referenceTransaction = dto.getReferenceTransaction() == null ? "MM-" + randomSuffix() : dto.getReferenceTransaction();
        TransactionMobileMoney existante = transactionMobileMoneyRepository.findByReferenceTransaction(referenceTransaction).orElse(null);
        if (existante != null) {
            return existante;
        }

        WalletClient wallet = walletClientRepository.findById(Long.valueOf(dto.getIdWalletClient()))
                .orElseThrow(() -> new EntityNotFoundException("Wallet client introuvable"));
        verifierPerimetreClient(wallet.getClient());

        validerTypeTransactionMobileMoney(dto.getTypeTransaction());

        TransactionMobileMoney transactionMobileMoney = new TransactionMobileMoney();
        transactionMobileMoney.setReferenceTransaction(referenceTransaction);
        transactionMobileMoney.setWalletClient(wallet);
        transactionMobileMoney.setTypeTransaction(dto.getTypeTransaction());
        transactionMobileMoney.setMontant(dto.getMontant());
        transactionMobileMoney.setFrais(dto.getFrais());
        transactionMobileMoney.setStatut(dto.getStatut() == null || dto.getStatut().isBlank() ? "INITIEE" : dto.getStatut());
        return transactionMobileMoneyRepository.save(transactionMobileMoney);
    }

    @Transactional
    public TransactionMobileMoney changerStatutTransactionMobileMoney(Long idTransactionMobileMoney, ChangerStatutTransactionMobileMoneyRequestDTO dto) {
        TransactionMobileMoney transactionMobileMoney = transactionMobileMoneyRepository.findById(idTransactionMobileMoney)
                .orElseThrow(() -> new EntityNotFoundException("Transaction mobile money introuvable"));
        verifierPerimetreClient(transactionMobileMoney.getWalletClient().getClient());

        transitionnerStatutMobileMoney(transactionMobileMoney, dto.getStatut());
        return transactionMobileMoneyRepository.save(transactionMobileMoney);
    }

    @Transactional
    public LotCompensation creerLotCompensation(CreerLotCompensationServiceRequestDTO dto) {
        LotCompensation lot = new LotCompensation();
        lot.setReferenceLot(dto.getReferenceLot() == null ? "LOT-" + randomSuffix() : dto.getReferenceLot());
        lot.setTypeLot(dto.getTypeLot());
        lot.setStatut(dto.getStatut());
        lot.setDateTraitement(dto.getDateTraitement() == null ? null : LocalDateTime.parse(dto.getDateTraitement()));
        lot.setCommentaire(dto.getCommentaire());
        return lotCompensationRepository.save(lot);
    }

    @Transactional
    public OrdrePaiementExterne initierOrdrePaiement(CreerOrdrePaiementServiceRequestDTO dto) {
        Compte compte = compteRepository.findById(Long.valueOf(dto.getIdCompte()))
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable"));
        verifierPerimetreClient(compte.getClient());

        OrdrePaiementExterne ordre = new OrdrePaiementExterne();
        ordre.setReferenceOrdre(dto.getReferenceOrdre() == null ? "PEX-" + randomSuffix() : dto.getReferenceOrdre());
        ordre.setTypeFlux(dto.getTypeFlux());
        ordre.setSens(dto.getSens());
        ordre.setMontant(dto.getMontant());
        ordre.setFrais(dto.getFrais());
        ordre.setCompte(compte);
        ordre.setReferenceExterne(dto.getReferenceExterne());
        ordre.setDestinationDetail(dto.getDestinationDetail());
        ordre.setDateInitiation(dto.getDateInitiation() == null ? LocalDateTime.now() : LocalDateTime.parse(dto.getDateInitiation()));
        ordre.setStatut(dto.getStatut());
        if (dto.getIdLotCompensation() != null) {
            LotCompensation lot = lotCompensationRepository.findById(Long.valueOf(dto.getIdLotCompensation()))
                    .orElseThrow(() -> new EntityNotFoundException("Lot de compensation introuvable"));
            ordre.setLotCompensation(lot);
        }
        return ordrePaiementExterneRepository.save(ordre);
    }

    @Transactional
    public OrdrePaiementExterne changerStatutOrdre(Long idOrdre, ChangerStatutOrdreServiceRequestDTO dto) {
        OrdrePaiementExterne ordre = ordrePaiementExterneRepository.findById(idOrdre)
                .orElseThrow(() -> new EntityNotFoundException("Ordre de paiement externe introuvable"));
        verifierPerimetreClient(ordre.getCompte().getClient());
        String nouveauStatut = dto.getStatut();

        if (("REGLE".equals(nouveauStatut) || "COMPENSE".equals(nouveauStatut)) && ordre.getReferenceTransactionInterne() == null) {
            appliquerReglementOrdre(ordre);
        }

        if ("RAPPROCHE".equals(nouveauStatut)) {
            if (ordre.getReferenceTransactionInterne() == null) {
                throw new IllegalStateException("Le rapprochement est impossible avant reglement comptable");
            }
            ordre.setDateRapprochement(LocalDateTime.now());
        }
        if (dto.getIdLotCompensation() != null) {
            LotCompensation lot = lotCompensationRepository.findById(Long.valueOf(dto.getIdLotCompensation()))
                    .orElseThrow(() -> new EntityNotFoundException("Lot de compensation introuvable"));
            ordre.setLotCompensation(lot);
        }
        ordre.setStatut(nouveauStatut);
        return ordrePaiementExterneRepository.save(ordre);
    }

    @Transactional
    public Map<String, Object> traiterReglementFinDeJournee(LocalDateTime dateLimite) {
        int mobileMoneyReglees = 0;
        for (TransactionMobileMoney transactionMobileMoney : transactionMobileMoneyRepository.findByStatutIgnoreCaseOrderByCreatedAtAsc("ACCEPTEE")) {
            if (transactionMobileMoney.getCreatedAt() != null && transactionMobileMoney.getCreatedAt().isAfter(dateLimite)) {
                continue;
            }
            transitionnerStatutMobileMoney(transactionMobileMoney, "REGLEE");
            transactionMobileMoneyRepository.save(transactionMobileMoney);
            mobileMoneyReglees++;
        }

        int ordresCompenses = 0;
        for (OrdrePaiementExterne ordre : ordrePaiementExterneRepository.findByStatutIgnoreCaseOrderByDateInitiationAsc("ACCEPTE")) {
            if (ordre.getDateInitiation() != null && ordre.getDateInitiation().isAfter(dateLimite)) {
                continue;
            }
            if (ordre.getReferenceTransactionInterne() == null) {
                appliquerReglementOrdre(ordre);
                ordrePaiementExterneRepository.save(ordre);
                ordresCompenses++;
            }
        }

        return Map.of(
                "mobileMoneyReglees", mobileMoneyReglees,
                "ordresCompenses", ordresCompenses
        );
    }

    @Transactional(readOnly = true)
    public List<OperateurMobileMoney> listerOperateurs() {
        return operateurMobileMoneyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<WalletClient> listerWallets() {
        return walletClientRepository.findAll().stream()
                .filter(wallet -> authenticatedUserService.hasGlobalScope()
                        || (authenticatedUserService.getCurrentAgencyId() != null
                        && wallet.getClient() != null
                        && wallet.getClient().getAgence() != null
                        && authenticatedUserService.getCurrentAgencyId().equals(wallet.getClient().getAgence().getIdAgence())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionMobileMoney> listerTransactionsMobileMoney() {
        return transactionMobileMoneyRepository.findAll().stream()
                .filter(transaction -> authenticatedUserService.hasGlobalScope()
                        || (authenticatedUserService.getCurrentAgencyId() != null
                        && transaction.getWalletClient() != null
                        && transaction.getWalletClient().getClient() != null
                        && transaction.getWalletClient().getClient().getAgence() != null
                        && authenticatedUserService.getCurrentAgencyId().equals(transaction.getWalletClient().getClient().getAgence().getIdAgence())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LotCompensation> listerLotsCompensation() {
        return lotCompensationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<OrdrePaiementExterne> listerOrdresPaiement() {
        return ordrePaiementExterneRepository.findAll().stream()
                .filter(ordre -> authenticatedUserService.hasGlobalScope()
                        || (authenticatedUserService.getCurrentAgencyId() != null
                        && ordre.getCompte() != null
                        && ordre.getCompte().getAgence() != null
                        && authenticatedUserService.getCurrentAgencyId().equals(ordre.getCompte().getAgence().getIdAgence())))
                .toList();
    }

    private String operationCodePourOrdre(String typeFlux) {
        return switch (typeFlux.toUpperCase()) {
            case "CHEQUE" -> "CHEQUE_ENCAISSEMENT";
            case "COMPENSATION" -> "COMPENSATION_SICA";
            case "RTGS" -> "VIREMENT_RTGS";
            case "MONETIQUE", "GAB" -> "MONETIQUE_REGLEMENT";
            default -> throw new IllegalArgumentException("Type de flux externe non supporte: " + typeFlux);
        };
    }

    private void validerTypeTransactionMobileMoney(String typeTransaction) {
        switch (typeTransaction) {
            case "CASH_IN", "CASH_OUT", "PAIEMENT_FACTURE", "RECHARGE_TELEPHONIQUE" -> {
                return;
            }
            default -> throw new IllegalArgumentException("Type mobile money non supporte: " + typeTransaction);
        }
    }

    private void transitionnerStatutMobileMoney(TransactionMobileMoney transactionMobileMoney, String nouveauStatut) {
        String statutCourant = transactionMobileMoney.getStatut() == null ? "INITIEE" : transactionMobileMoney.getStatut().toUpperCase();
        if ("REJETEE".equals(statutCourant) || "ANNULEE".equals(statutCourant) || "RAPPROCHEE".equals(statutCourant)) {
            throw new IllegalStateException("La transaction mobile money est deja finalisee");
        }
        if ("REGLEE".equals(nouveauStatut) || "COMPENSEE".equals(nouveauStatut) || "RAPPROCHEE".equals(nouveauStatut)) {
            comptabiliserTransactionMobileMoneySiNecessaire(transactionMobileMoney);
        }
        transactionMobileMoney.setStatut(nouveauStatut);
    }

    private void comptabiliserTransactionMobileMoneySiNecessaire(TransactionMobileMoney transactionMobileMoney) {
        if (transactionMobileMoney.getReferenceTransactionInterne() != null) {
            return;
        }
        Long idUtilisateurOperateur = authenticatedUserService.getCurrentUserOrThrow().getIdUser();
        var transactionInterne = switch (transactionMobileMoney.getTypeTransaction().toUpperCase()) {
            case "CASH_IN" -> transactionService.posterDepotSysteme(
                    transactionMobileMoney.getWalletClient().getCompte().getNumCompte(),
                    transactionMobileMoney.getMontant(),
                    transactionMobileMoney.getFrais(),
                    idUtilisateurOperateur,
                    transactionMobileMoney.getReferenceTransaction(),
                    "MOBILEMONEY_CASHIN"
            );
            case "CASH_OUT" -> transactionService.posterRetraitSysteme(
                    transactionMobileMoney.getWalletClient().getCompte().getNumCompte(),
                    transactionMobileMoney.getMontant(),
                    transactionMobileMoney.getFrais(),
                    idUtilisateurOperateur,
                    transactionMobileMoney.getReferenceTransaction(),
                    "MOBILEMONEY_CASHOUT"
            );
            case "PAIEMENT_FACTURE" -> transactionService.posterRetraitSysteme(
                    transactionMobileMoney.getWalletClient().getCompte().getNumCompte(),
                    transactionMobileMoney.getMontant(),
                    transactionMobileMoney.getFrais(),
                    idUtilisateurOperateur,
                    transactionMobileMoney.getReferenceTransaction(),
                    "PAIEMENT_FACTURE"
            );
            case "RECHARGE_TELEPHONIQUE" -> transactionService.posterRetraitSysteme(
                    transactionMobileMoney.getWalletClient().getCompte().getNumCompte(),
                    transactionMobileMoney.getMontant(),
                    transactionMobileMoney.getFrais(),
                    idUtilisateurOperateur,
                    transactionMobileMoney.getReferenceTransaction(),
                    "RECHARGE_TELEPHONIQUE"
            );
            default -> throw new IllegalArgumentException("Type mobile money non supporte: " + transactionMobileMoney.getTypeTransaction());
        };
        transactionMobileMoney.setReferenceTransactionInterne(transactionInterne.getReferenceUnique());
    }

    private void appliquerReglementOrdre(OrdrePaiementExterne ordre) {
        Long idUtilisateurOperateur = authenticatedUserService.getCurrentUserOrThrow().getIdUser();
        String operationCode = operationCodePourOrdre(ordre.getTypeFlux());
        var transactionInterne = "CREDIT_CLIENT".equalsIgnoreCase(ordre.getSens())
                ? transactionService.posterDepotSysteme(ordre.getCompte().getNumCompte(), ordre.getMontant(), ordre.getFrais(), idUtilisateurOperateur, ordre.getReferenceOrdre(), operationCode)
                : transactionService.posterRetraitSysteme(ordre.getCompte().getNumCompte(), ordre.getMontant(), ordre.getFrais(), idUtilisateurOperateur, ordre.getReferenceOrdre(), operationCode);
        ordre.setReferenceTransactionInterne(transactionInterne.getReferenceUnique());
        ordre.setDateReglement(LocalDateTime.now());
        if ("ACCEPTE".equalsIgnoreCase(ordre.getStatut()) || "INITIE".equalsIgnoreCase(ordre.getStatut())) {
            ordre.setStatut("REGLE");
        }
    }

    private void verifierPerimetreClient(Client client) {
        if (client == null || client.getAgence() == null) {
            return;
        }
        authenticatedUserService.assertAgencyAccess(client.getAgence().getIdAgence());
    }

    private String randomSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
