package com.microfinance.core_banking.service.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.extension.CreerLotCompensationServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerOrdrePaiementServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerWalletServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.LotCompensation;
import com.microfinance.core_banking.entity.OrdrePaiementExterne;
import com.microfinance.core_banking.entity.ResultatStressTest;
import com.microfinance.core_banking.entity.WalletClient;
import com.microfinance.core_banking.service.extension.action.PendingActionHandler;
import com.microfinance.core_banking.service.extension.action.PendingActionHandlerRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PendingActionExecutionServiceTest {

    @Mock
    private PendingActionHandlerRegistry handlerRegistry;
    @Mock
    private PendingActionHandler lotHandler;
    @Mock
    private PendingActionHandler stressHandler;
    @Mock
    private PendingActionHandler walletHandler;
    @Mock
    private PendingActionHandler ordreHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void execute_shouldDispatchCompensationBatchCreationToExternalPaymentService() {
        PendingActionExecutionService pendingActionExecutionService = buildService();
        LotCompensation lot = new LotCompensation();
        lot.setIdLotCompensation(22L);

        when(handlerRegistry.getHandler("CREATE_LOT_COMPENSATION")).thenReturn(lotHandler);
        when(lotHandler.execute(any(), anyMap())).thenReturn("22");

        ActionEnAttente action = new ActionEnAttente();
        action.setTypeAction("CREATE_LOT_COMPENSATION");
        action.setNouvelleValeur("{\"typeLot\":\"SICA\"}");

        String result = pendingActionExecutionService.execute(action);

        assertThat(result).isEqualTo("22");
        verify(lotHandler).execute(eq(action), anyMap());
    }

    @Test
    void execute_shouldDispatchStressExecutionToRiskService() {
        PendingActionExecutionService pendingActionExecutionService = buildService();

        when(handlerRegistry.getHandler("EXECUTE_STRESS_TEST")).thenReturn(stressHandler);
        when(stressHandler.execute(any(), anyMap())).thenReturn("33");

        ActionEnAttente action = new ActionEnAttente();
        action.setTypeAction("EXECUTE_STRESS_TEST");
        action.setReferenceRessource("9");
        action.setNouvelleValeur("{\"idStressTest\":9}");

        String result = pendingActionExecutionService.execute(action);

        assertThat(result).isEqualTo("33");
        verify(stressHandler).execute(eq(action), anyMap());
    }

    @Test
    void execute_shouldDispatchWalletCreationToExternalPaymentService() {
        PendingActionExecutionService pendingActionExecutionService = buildService();

        when(handlerRegistry.getHandler("CREATE_WALLET_CLIENT")).thenReturn(walletHandler);
        when(walletHandler.execute(any(), anyMap())).thenReturn("41");

        ActionEnAttente action = new ActionEnAttente();
        action.setTypeAction("CREATE_WALLET_CLIENT");
        action.setNouvelleValeur("{\"idClient\":7,\"idOperateurMobileMoney\":2,\"idCompte\":5,\"numeroWallet\":\"2250700000000\"}");

        String result = pendingActionExecutionService.execute(action);

        assertThat(result).isEqualTo("41");
        verify(walletHandler).execute(eq(action), anyMap());
    }

    @Test
    void execute_shouldDispatchExternalOrderCreationToExternalPaymentService() {
        PendingActionExecutionService pendingActionExecutionService = buildService();

        when(handlerRegistry.getHandler("CREATE_ORDRE_PAIEMENT_EXTERNE")).thenReturn(ordreHandler);
        when(ordreHandler.execute(any(), anyMap())).thenReturn("52");

        ActionEnAttente action = new ActionEnAttente();
        action.setTypeAction("CREATE_ORDRE_PAIEMENT_EXTERNE");
        action.setNouvelleValeur("{\"idCompte\":12,\"typeFlux\":\"RTGS\",\"montant\":15000}");

        String result = pendingActionExecutionService.execute(action);

        assertThat(result).isEqualTo("52");
        verify(ordreHandler).execute(eq(action), anyMap());
    }

    private PendingActionExecutionService buildService() {
        return new PendingActionExecutionService(objectMapper, handlerRegistry);
    }
}
