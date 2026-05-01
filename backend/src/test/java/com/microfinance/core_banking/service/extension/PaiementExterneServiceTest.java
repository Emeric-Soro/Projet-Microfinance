package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.OperateurMobileMoney;
import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.entity.TransactionMobileMoney;
import com.microfinance.core_banking.entity.Utilisateur;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaiementExterneServiceTest {

    @Mock
    private OperateurMobileMoneyRepository operateurMobileMoneyRepository;
    @Mock
    private WalletClientRepository walletClientRepository;
    @Mock
    private TransactionMobileMoneyRepository transactionMobileMoneyRepository;
    @Mock
    private LotCompensationRepository lotCompensationRepository;
    @Mock
    private OrdrePaiementExterneRepository ordrePaiementExterneRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private CompteRepository compteRepository;
    @Mock
    private TransactionService transactionService;
    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private PaiementExterneService paiementExterneService;

    @Test
    void enregistrerTransactionMobileMoney_nePostePasAvantReglement() {
        WalletClient wallet = wallet();
        when(transactionMobileMoneyRepository.findByReferenceTransaction("MM-001")).thenReturn(Optional.empty());
        when(walletClientRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(transactionMobileMoneyRepository.save(any(TransactionMobileMoney.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionMobileMoney resultat = paiementExterneService.enregistrerTransactionMobileMoney(Map.of(
                "referenceTransaction", "MM-001",
                "idWalletClient", 1L,
                "typeTransaction", "CASH_IN",
                "montant", "10000",
                "frais", "250"
        ));

        assertThat(resultat.getStatut()).isEqualTo("INITIEE");
        assertThat(resultat.getReferenceTransactionInterne()).isNull();
        verify(transactionService, never()).posterDepotSysteme(any(), any(), any(), any(), any(), any());
        verify(transactionService, never()).posterRetraitSysteme(any(), any(), any(), any(), any(), any());
    }

    @Test
    void changerStatutTransactionMobileMoney_regleeDeclencheLaComptabilisation() {
        WalletClient wallet = wallet();
        TransactionMobileMoney transactionMobileMoney = new TransactionMobileMoney();
        transactionMobileMoney.setIdTransactionMobileMoney(5L);
        transactionMobileMoney.setReferenceTransaction("MM-SETTLE-01");
        transactionMobileMoney.setWalletClient(wallet);
        transactionMobileMoney.setTypeTransaction("CASH_OUT");
        transactionMobileMoney.setMontant(new BigDecimal("5000"));
        transactionMobileMoney.setFrais(new BigDecimal("100"));
        transactionMobileMoney.setStatut("ACCEPTEE");

        Utilisateur checker = new Utilisateur();
        checker.setIdUser(99L);
        Transaction transactionInterne = new Transaction();
        transactionInterne.setReferenceUnique("TX-INT-01");

        when(transactionMobileMoneyRepository.findById(5L)).thenReturn(Optional.of(transactionMobileMoney));
        when(authenticatedUserService.getCurrentUserOrThrow()).thenReturn(checker);
        when(transactionService.posterRetraitSysteme(any(), any(), any(), any(), any(), any())).thenReturn(transactionInterne);
        when(transactionMobileMoneyRepository.save(any(TransactionMobileMoney.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionMobileMoney resultat = paiementExterneService.changerStatutTransactionMobileMoney(5L, Map.of("statut", "REGLEE"));

        assertThat(resultat.getStatut()).isEqualTo("REGLEE");
        assertThat(resultat.getReferenceTransactionInterne()).isEqualTo("TX-INT-01");
        verify(transactionService).posterRetraitSysteme(
                wallet.getCompte().getNumCompte(),
                new BigDecimal("5000"),
                new BigDecimal("100"),
                99L,
                "MM-SETTLE-01",
                "MOBILEMONEY_CASHOUT"
        );
    }

    private WalletClient wallet() {
        Client client = new Client();
        client.setIdClient(10L);
        client.setNom("Diallo");
        client.setPrenom("Awa");

        Compte compte = new Compte();
        compte.setIdCompte(15L);
        compte.setNumCompte("CPT-100");
        compte.setClient(client);

        OperateurMobileMoney operateur = new OperateurMobileMoney();
        operateur.setIdOperateurMobileMoney(30L);
        operateur.setNomOperateur("Wave");

        WalletClient wallet = new WalletClient();
        wallet.setIdWalletClient(1L);
        wallet.setClient(client);
        wallet.setCompte(compte);
        wallet.setOperateurMobileMoney(operateur);
        wallet.setNumeroWallet("770000000");
        return wallet;
    }
}
