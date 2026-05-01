package com.microfinance.core_banking.service.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.LotCompensation;
import com.microfinance.core_banking.entity.OrdrePaiementExterne;
import com.microfinance.core_banking.entity.ResultatStressTest;
import com.microfinance.core_banking.entity.WalletClient;
import com.microfinance.core_banking.repository.extension.LoanFacilityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PendingActionExecutionServiceTest {

    @Mock
    private LoanFacilityRepository loanFacilityRepository;
    @Mock
    private OrganisationService organisationService;
    @Mock
    private CreditExtensionService creditExtensionService;
    @Mock
    private EpargneExtensionService epargneExtensionService;
    @Mock
    private TresorerieService tresorerieService;
    @Mock
    private DigitalExtensionService digitalExtensionService;
    @Mock
    private ComptabiliteExtensionService comptabiliteExtensionService;
    @Mock
    private PermissionSecuriteService permissionSecuriteService;
    @Mock
    private RoleUtilisateurService roleUtilisateurService;
    @Mock
    private ConformiteExtensionService conformiteExtensionService;
    @Mock
    private SupportEntrepriseService supportEntrepriseService;
    @Mock
    private PaiementExterneService paiementExterneService;
    @Mock
    private RisqueExtensionService risqueExtensionService;

    @Test
    void execute_shouldDispatchCompensationBatchCreationToExternalPaymentService() {
        PendingActionExecutionService pendingActionExecutionService = buildService();
        LotCompensation lot = new LotCompensation();
        lot.setIdLotCompensation(22L);

        when(paiementExterneService.creerLotCompensation(Map.of("typeLot", "SICA"))).thenReturn(lot);

        ActionEnAttente action = new ActionEnAttente();
        action.setTypeAction("CREATE_LOT_COMPENSATION");
        action.setNouvelleValeur("{\"typeLot\":\"SICA\"}");

        String result = pendingActionExecutionService.execute(action);

        assertThat(result).isEqualTo("22");
        verify(paiementExterneService).creerLotCompensation(Map.of("typeLot", "SICA"));
    }

    @Test
    void execute_shouldDispatchStressExecutionToRiskService() {
        PendingActionExecutionService pendingActionExecutionService = buildService();
        ResultatStressTest resultat = new ResultatStressTest();
        resultat.setIdResultatStressTest(33L);

        when(risqueExtensionService.executerStressTest(9L)).thenReturn(resultat);

        ActionEnAttente action = new ActionEnAttente();
        action.setTypeAction("EXECUTE_STRESS_TEST");
        action.setReferenceRessource("9");
        action.setNouvelleValeur("{\"idStressTest\":9}");

        String result = pendingActionExecutionService.execute(action);

        assertThat(result).isEqualTo("33");
        verify(risqueExtensionService).executerStressTest(9L);
    }

    @Test
    void execute_shouldDispatchWalletCreationToExternalPaymentService() {
        PendingActionExecutionService pendingActionExecutionService = buildService();
        WalletClient walletClient = new WalletClient();
        walletClient.setIdWalletClient(41L);

        when(paiementExterneService.creerWallet(Map.of(
                "idClient", 7,
                "idOperateurMobileMoney", 2,
                "idCompte", 5,
                "numeroWallet", "2250700000000"
        ))).thenReturn(walletClient);

        ActionEnAttente action = new ActionEnAttente();
        action.setTypeAction("CREATE_WALLET_CLIENT");
        action.setNouvelleValeur("{\"idClient\":7,\"idOperateurMobileMoney\":2,\"idCompte\":5,\"numeroWallet\":\"2250700000000\"}");

        String result = pendingActionExecutionService.execute(action);

        assertThat(result).isEqualTo("41");
        verify(paiementExterneService).creerWallet(Map.of(
                "idClient", 7,
                "idOperateurMobileMoney", 2,
                "idCompte", 5,
                "numeroWallet", "2250700000000"
        ));
    }

    @Test
    void execute_shouldDispatchExternalOrderCreationToExternalPaymentService() {
        PendingActionExecutionService pendingActionExecutionService = buildService();
        OrdrePaiementExterne ordre = new OrdrePaiementExterne();
        ordre.setIdOrdrePaiementExterne(52L);

        when(paiementExterneService.initierOrdrePaiement(Map.of(
                "idCompte", 12,
                "typeFlux", "RTGS",
                "montant", 15000
        ))).thenReturn(ordre);

        ActionEnAttente action = new ActionEnAttente();
        action.setTypeAction("CREATE_ORDRE_PAIEMENT_EXTERNE");
        action.setNouvelleValeur("{\"idCompte\":12,\"typeFlux\":\"RTGS\",\"montant\":15000}");

        String result = pendingActionExecutionService.execute(action);

        assertThat(result).isEqualTo("52");
        verify(paiementExterneService).initierOrdrePaiement(Map.of(
                "idCompte", 12,
                "typeFlux", "RTGS",
                "montant", 15000
        ));
    }

    private PendingActionExecutionService buildService() {
        return new PendingActionExecutionService(
                new ObjectMapper(),
                organisationService,
                creditExtensionService,
                epargneExtensionService,
                tresorerieService,
                digitalExtensionService,
                comptabiliteExtensionService,
                permissionSecuriteService,
                roleUtilisateurService,
                conformiteExtensionService,
                paiementExterneService,
                risqueExtensionService,
                supportEntrepriseService,
                loanFacilityRepository
        );
    }
}
